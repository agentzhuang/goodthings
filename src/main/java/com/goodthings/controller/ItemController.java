package com.goodthings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.goodthings.common.BizException;
import com.goodthings.common.Result;
import com.goodthings.dto.CreateItemDTO;
import com.goodthings.entity.*;
import com.goodthings.mapper.*;
import com.goodthings.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final CmsItemMapper itemMapper;
    private final CmsItemTagMapper itemTagMapper;
    private final CmsLikeMapper likeMapper;
    private final CmsCommentMapper commentMapper;
    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;

    @PostMapping
    public Result<?> create(@RequestHeader("Authorization") String authHeader,
                            @RequestBody CreateItemDTO dto) {
        Long userId = getUserIdFromHeader(authHeader);

        CmsItem item = new CmsItem();
        item.setUserId(userId);
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setMediaType(dto.getMediaType());
        item.setCoverUrl(dto.getCoverUrl());
        item.setMediaUrl(dto.getMediaUrl());
        item.setWidth(dto.getWidth());
        item.setHeight(dto.getHeight());
        item.setDuration(dto.getDuration());
        item.setLikeCount(0);
        item.setCommentCount(0);
        item.setStatus(1);
        itemMapper.insert(item);

        // 保存标签关联
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            for (Long tagId : dto.getTagIds()) {
                CmsItemTag itemTag = new CmsItemTag();
                itemTag.setItemId(item.getId());
                itemTag.setTagId(tagId);
                itemTagMapper.insert(itemTag);
            }
        }

        return Result.success("发布成功", java.util.Collections.singletonMap("itemId", item.getId()));
    }

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long tagId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(required = false) Long userId) {

        Page<CmsItem> pageParam = new Page<>(page, Math.min(size, 50));

        LambdaQueryWrapper<CmsItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CmsItem::getStatus, 1);
        if (userId != null) {
            wrapper.eq(CmsItem::getUserId, userId);
        }
        if ("hot".equals(sort)) {
            wrapper.orderByDesc(CmsItem::getLikeCount);
        } else {
            wrapper.orderByDesc(CmsItem::getCreateTime);
        }

        IPage<CmsItem> result = itemMapper.selectPage(pageParam, wrapper);

        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        CmsItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException("收藏品不存在");
        }

        // 查询用户信息
        SysUser user = userMapper.selectById(item.getUserId());

        // 查询标签
        List<CmsItemTag> itemTags = itemTagMapper.selectList(
                new LambdaQueryWrapper<CmsItemTag>().eq(CmsItemTag::getItemId, id)
        );
        List<Long> tagIds = itemTags.stream().map(CmsItemTag::getTagId).collect(Collectors.toList());

        return Result.success(java.util.Collections.singletonMap("item", item));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@RequestHeader("Authorization") String authHeader,
                            @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authHeader);
        CmsItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException("收藏品不存在");
        }
        if (!item.getUserId().equals(userId)) {
            throw new BizException(403, "无权限删除");
        }
        // cascade delete related records
        itemTagMapper.delete(new LambdaQueryWrapper<CmsItemTag>().eq(CmsItemTag::getItemId, id));
        likeMapper.delete(new LambdaQueryWrapper<CmsLike>().eq(CmsLike::getItemId, id));
        commentMapper.delete(new LambdaQueryWrapper<CmsComment>().eq(CmsComment::getItemId, id));
        itemMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/like")
    public Result<?> like(@RequestHeader("Authorization") String authHeader,
                          @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authHeader);

        // 检查收藏品是否存在
        CmsItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException("收藏品不存在");
        }

        // 检查是否已点赞
        CmsLike existLike = likeMapper.selectOne(
                new LambdaQueryWrapper<CmsLike>()
                        .eq(CmsLike::getUserId, userId)
                        .eq(CmsLike::getItemId, id)
        );
        if (existLike != null) {
            throw new BizException("已点赞");
        }

        CmsLike like = new CmsLike();
        like.setUserId(userId);
        like.setItemId(id);
        likeMapper.insert(like);

        // 更新点赞数
        itemMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CmsItem>()
                        .eq(CmsItem::getId, id)
                        .setSql("like_count = like_count + 1")
        );

        return Result.success("点赞成功");
    }

    @DeleteMapping("/{id}/like")
    public Result<?> unlike(@RequestHeader("Authorization") String authHeader,
                           @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authHeader);

        // 检查收藏品是否存在
        CmsItem item = itemMapper.selectById(id);
        if (item == null) {
            throw new BizException("收藏品不存在");
        }

        likeMapper.delete(
                new LambdaQueryWrapper<CmsLike>()
                        .eq(CmsLike::getUserId, userId)
                        .eq(CmsLike::getItemId, id)
        );

        // 更新点赞数
        itemMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CmsItem>()
                        .eq(CmsItem::getId, id)
                        .setSql("like_count = like_count - 1")
        );

        return Result.success("取消点赞成功");
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
