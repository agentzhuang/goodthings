package com.goodthings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.goodthings.common.BizException;
import com.goodthings.common.Result;
import com.goodthings.entity.CmsTag;
import com.goodthings.mapper.CmsTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/tags")
@RequiredArgsConstructor
public class TagController {

    private final CmsTagMapper tagMapper;

    @GetMapping("/hot")
    public Result<?> hot(@RequestParam(defaultValue = "10") Integer limit) {
        List<CmsTag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<CmsTag>()
                        .orderByDesc(CmsTag::getUseCount)
                        .last("LIMIT " + limit)
        );
        return Result.success(tags);
    }

    @GetMapping("/search")
    public Result<?> search(@RequestParam String keyword) {
        List<CmsTag> tags = tagMapper.selectList(
                new LambdaQueryWrapper<CmsTag>()
                        .like(CmsTag::getName, keyword)
                        .orderByDesc(CmsTag::getUseCount)
                        .last("LIMIT 20")
        );
        return Result.success(tags);
    }

    @PostMapping
    public Result<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody CmsTag tag) {
        validateAuth(authHeader);
        tagMapper.insert(tag);
        return Result.success("创建成功", java.util.Collections.singletonMap("tagId", tag.getId()));
    }

    @PostMapping("/items/{itemId}")
    public Result<?> bindItem(@RequestHeader("Authorization") String authHeader, @PathVariable Long itemId, @RequestBody List<Long> tagIds) {
        validateAuth(authHeader);
        // 更新标签使用次数
        for (Long tagId : tagIds) {
            tagMapper.update(null,
                    new LambdaUpdateWrapper<CmsTag>()
                            .eq(CmsTag::getId, tagId)
                            .setSql("use_count = use_count + 1")
            );
        }
        return Result.success("绑定成功");
    }

    private void validateAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
    }
}
