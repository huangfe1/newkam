package com.dreamer.service.mobile;

import com.dreamer.domain.data.FileData;
import ps.mx.otter.utils.SearchParameter;

import java.util.List;

public interface FileDataHandler extends BaseHandler<FileData>{

    List<FileData> findBypage(SearchParameter<FileData> fileData);

}
