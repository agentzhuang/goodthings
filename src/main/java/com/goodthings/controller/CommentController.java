package com.goodthings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodthings.common.BizException;
import com.goodthings.common.Result;
import com.goodthings.dto.CreateCommentDTO;
import com.goodthings.entity.CmsComment;
import com.goodthings.mapper.CmsCommentMapper;
import com.goodthings.mapper.CmsItemMapper;
import com.goodthings.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/items/{itemId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CmsCommentMapper commentMapper;
    private final CmsItemMapper itemMapper;
    private final JwtUtil jwtUtil;

    @GetMapping
    public Result<?> list(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        Page<CmsComment> pageParam = new Page<>(page, Math.min(size, 50));
        LambdaQueryWrapper<CmsComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsComment::getItemId, itemId);
        wrapper.eq(CmsComment::getStatus, 1);
        wrapper.orderByDesc(CmsComment::getCreateTime);

        IPage<CmsComment> result = commentMapper.selectPage(pageParam, wrapper);
        return Result.success(result);
    }

    @PostMapping
    public Result<?> create(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long itemId,
            @RequestBody CreateCommentDTO dto) {

        Long userId = getUserIdFromHeader(authHeader);

        CmsComment comment = new CmsComment();
        comment.setItemId(itemId);
        comment.setUserId(userId);
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        comment.setLikeCount(0);
        comment.setStatus(1);
        commentMapper.insert(comment);

        // 更新评论数
        itemMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.goodthings.entity.CmsItem>()
                        .eq(com.goodthings.entity.CmsItem::getId, itemId)
                        .setSql("comment_count = comment_count + 1")
        );

        return Result.success("评论成功", java.util.Collections.singletonMap("commentId", comment.getId()));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long itemId,
            @PathVariable Long id) {

        Long userId = getUserIdFromHeader(authHeader);
        CmsComment comment = commentMapper.selectById(id);

        if (comment == null) {
            throw new BizException("评论不存在");
        }
        if (!comment.getUserId().equals(userId)) {
            throw new BizException(403, "无权限删除");
        }

        commentMapper.deleteById(id);

        // 更新评论数
        itemMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<com.goodthings.entity.CmsItem>()
                        .eq(com.goodthings.entity.CmsItem::getId, itemId)
                        .setSql("comment_count = comment_count - 1")
        );

        return Result.success("删除成功");
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
