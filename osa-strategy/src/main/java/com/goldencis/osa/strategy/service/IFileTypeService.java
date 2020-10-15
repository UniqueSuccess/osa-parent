package com.goldencis.osa.strategy.service;

import com.goldencis.osa.strategy.entity.FileType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author limingchao
 * @since 2019-01-23
 */
public interface IFileTypeService extends IService<FileType> {

    List<FileType> getEnabledFileTypeList();
}
