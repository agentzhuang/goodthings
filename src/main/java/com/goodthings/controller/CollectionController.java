package com.goodthings.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.goodthings.common.BizException;
import com.goodthings.common.Result;
import com.goodthings.dto.CreateCollectionDTO;
import com.goodthings.entity.*;
import com.goodthings.mapper.*;
import com.goodthings.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CmsCollectionMapper collectionMapper;
    private final CmsItemCollectionMapper itemCollectionMapper;
    private final CmsItemMapper itemMapper;
    private final JwtUtil jwtUtil;

    @PostMapping
    public Result<?> create(@RequestHeader("Authorization") String authHeader,
                            @RequestBody CreateCollectionDTO dto) {
        Long userId = getUserIdFromHeader(authHeader);

        CmsCollection collection = new CmsCollection();
        collection.setUserId(userId);
        collection.setName(dto.getName());
        collection.setDescription(dto.getDescription());
        collection.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : 0);
        collection.setItemCount(0);
        collectionMapper.insert(collection);

        Map<String, Object> data = new HashMap<>();
        data.put("collectionId", collection.getId());
        return Result.success("创建成功", data);
    }

    @GetMapping("/me")
    public Result<?> myList(@RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        List<CmsCollection> collections = collectionMapper.selectList(
                new LambdaQueryWrapper<CmsCollection>()
                        .eq(CmsCollection::getUserId, userId)
                        .orderByDesc(CmsCollection::getCreateTime)
        );
        return Result.success(collections);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        CmsCollection collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw new BizException("收藏夹不存在");
        }
        return Result.success(collection);
    }

    @PutMapping("/{id}")
    public Result<?> update(@RequestHeader("Authorization") String authHeader,
                            @PathVariable Long id,
                            @RequestBody CreateCollectionDTO dto) {
        Long userId = getUserIdFromHeader(authHeader);
        CmsCollection collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw new BizException("收藏夹不存在");
        }
        if (!collection.getUserId().equals(userId)) {
            throw new BizException(403, "无权限修改");
        }
        if (dto.getName() != null) collection.setName(dto.getName());
        if (dto.getDescription() != null) collection.setDescription(dto.getDescription());
        if (dto.getIsPublic() != null) collection.setIsPublic(dto.getIsPublic());
        collectionMapper.updateById(collection);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@RequestHeader("Authorization") String authHeader,
                            @PathVariable Long id) {
        Long userId = getUserIdFromHeader(authHeader);
        CmsCollection collection = collectionMapper.selectById(id);
        if (collection == null) {
            throw new BizException("收藏夹不存在");
        }
        if (!collection.getUserId().equals(userId)) {
            throw new BizException(403, "无权限删除");
        }
        collectionMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @PostMapping("/{id}/items")
    public Result<?> addItem(@RequestHeader("Authorization") String authHeader,
                             @PathVariable Long id,
                             @RequestBody Map<String, Long> body) {
        Long userId = getUserIdFromHeader(authHeader);
        CmsCollection collection = collectionMapper.selectById(id);
        if (collection == null || !collection.getUserId().equals(userId)) {
            throw new BizException("收藏夹不存在或无权限");
        }

        Long itemId = body.get("itemId");
        // 检查是否已添加
        CmsItemCollection exist = itemCollectionMapper.selectOne(
                new LambdaQueryWrapper<CmsItemCollection>()
                        .eq(CmsItemCollection::getItemId, itemId)
                        .eq(CmsItemCollection::getCollectionId, id)
        );
        if (exist != null) {
            throw new BizException("已添加过此收藏品");
        }

        CmsItemCollection ic = new CmsItemCollection();
        ic.setItemId(itemId);
        ic.setCollectionId(id);
        itemCollectionMapper.insert(ic);

        // 更新收藏品数量
        collectionMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CmsCollection>()
                        .eq(CmsCollection::getId, id)
                        .setSql("item_count = item_count + 1")
        );

        return Result.success("添加成功");
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public Result<?> removeItem(@RequestHeader("Authorization") String authHeader,
                                @PathVariable Long id,
                                @PathVariable Long itemId) {
        Long userId = getUserIdFromHeader(authHeader);
        CmsCollection collection = collectionMapper.selectById(id);
        if (collection == null || !collection.getUserId().equals(userId)) {
            throw new BizException("收藏夹不存在或无权限");
        }

        itemCollectionMapper.delete(
                new LambdaQueryWrapper<CmsItemCollection>()
                        .eq(CmsItemCollection::getItemId, itemId)
                        .eq(CmsItemCollection::getCollectionId, id)
        );

        // 更新收藏品数量
        collectionMapper.update(null,
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<CmsCollection>()
                        .eq(CmsCollection::getId, id)
                        .setSql("item_count = item_count - 1")
        );

        return Result.success("移除成功");
    }

    private Long getUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(401, "未登录");
        }
        String token = authHeader.substring(7);
        return jwtUtil.getUserIdFromToken(token);
    }
}
