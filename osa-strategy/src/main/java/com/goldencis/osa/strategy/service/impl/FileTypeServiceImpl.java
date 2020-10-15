package com.goldencis.osa.strategy.service.impl;

import com.goldencis.osa.strategy.entity.FileType;
import com.goldencis.osa.strategy.mapper.FileTypeMapper;
import com.goldencis.osa.strategy.service.IFileTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author limingchao
 * @since 2019-01-23
 */
@Service
public class FileTypeServiceImpl extends ServiceImpl<FileTypeMapper, FileType> implements IFileTypeService {

    @Override
    public List<FileType> getEnabledFileTypeList() {
        List<FileType> list = baseMapper.selectList(null);
        Map<Integer, FileType> map = list.stream().collect(Collectors.toMap(FileType::getId, assetType -> assetType));
        List<FileType> collect = list.stream()
                .filter(item -> {
                    // 过滤掉不启用的设备类型;
                    // 如果大类型不启用,小类型也要过滤掉;
                    // 层级关系目前只有两级;
                    Boolean status = item.getStatus();
                    // 先将明确不启用的过滤出去
                    if (Objects.nonNull(status) && !status) {
                        return false;
                    }
                    Integer pid = item.getPid();
                    if (Objects.isNull(pid)) {
                        return true;
                    }
                    FileType parent = map.get(pid);
                    // 如果大类型不启用,将小类型也过滤掉
                    return Objects.isNull(parent.getStatus()) || parent.getStatus();
                }).collect(Collectors.toList());

        return collect;
    }
}
