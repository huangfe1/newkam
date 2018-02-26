package com.dreamer.service.mobile.impl;

import com.dreamer.domain.data.FileData;
import com.dreamer.repository.data.FileDataDao;
import com.dreamer.service.mobile.FileDataHandler;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

@Service
public class FileDataHandlerImpl extends BaseHandlerImpl<FileData> implements FileDataHandler {

    @Override
    public List<FileData> findBypage(SearchParameter<FileData> fileData) {
        DetachedCriteria criteria = DetachedCriteria.forClass(fileData.getEntity().getClass());
        Example example = Example.create(fileData.getEntity()).enableLike(MatchMode.ANYWHERE);
        criteria.add(example);
        criteria.addOrder(Order.desc("id"));//倒序
        return fileDataDao.searchByPage(fileData, criteria);
    }

    private FileDataDao fileDataDao;

    public FileDataDao getFileDataDao() {
        return fileDataDao;
    }

    @Autowired
    public void setFileDataDao(FileDataDao fileDataDao) {
        super.setBaseDao(fileDataDao);
        this.fileDataDao = fileDataDao;
    }
}
