package com.movie.api.service;

import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Worker;
import com.movie.api.model.vo.WorkerPublicVO;

import java.util.List;

public interface WorkerService {

    Worker create(Worker worker) throws Exception;

    Worker login(LoginDto dto) throws Exception;

    void update(Worker worker) throws Exception;

    List<Worker> findAll();

    /** 留言页等：仅在职客服，无敏感信息 */
    List<WorkerPublicVO> listPublicShowcase();

    Worker findById(String id);

    void deleteById(String id);

}
