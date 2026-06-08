package com.movie.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.movie.api.constant.Roles;
import com.movie.api.mapper.RoleMapper;
import com.movie.api.mapper.WorkerMapper;
import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Role;
import com.movie.api.model.entity.Worker;
import com.movie.api.model.vo.WorkerPublicVO;
import com.movie.api.service.RoleService;
import com.movie.api.service.WorkerService;
import com.movie.api.utils.DataTimeUtil;
import com.movie.api.utils.ValidationUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.movie.api.model.vo.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class WorkerServiceImpl implements WorkerService {

    @Resource
    private WorkerMapper workerMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    // 创建员工（含默认权限ROLE_WORKER）
    @Override
    public Worker create(Worker worker) throws Exception {
        ValidationUtil.requireValidMobileCN(worker.getPhone(), "联系电话");
        if (workerMapper.selectOne(new QueryWrapper<Worker>().in("username", worker.getUsername())) != null) {
            throw new Exception("已存在的用户名");
        }
        String now = DataTimeUtil.getNowTimeString();
        worker.setEntry(true);
        worker.setPassword(bCryptPasswordEncoder.encode(worker.getPassword()));
        worker.setId(UUID.randomUUID().toString());
        worker.setCreateAt(now);
        worker.setUpdateAt(now);
        workerMapper.insert(worker);
        //添加worker权限
        roleMapper.insert(new Role(UUID.randomUUID().toString(), worker.getId(), Roles.ROLE_WORKER, now));
        return worker;
    }

    // 员工登录验证
    @Override
    public Worker login(LoginDto dto) throws Exception {
        QueryWrapper<Worker> wrapper = new QueryWrapper<>();
        wrapper.in("username", dto.getUsername());
        Worker worker = workerMapper.selectOne(wrapper);
        if (worker == null) throw new Exception("用户不存在");
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), worker.getPassword()))
            throw new Exception("密码错误");
        return worker;
    }

    // 更新员工信息
    @Override
    public void update(Worker worker) throws Exception {
        ValidationUtil.requireValidMobileCN(worker.getPhone(), "联系电话");
        Worker one = workerMapper.selectOne(new QueryWrapper<Worker>().in("username", worker.getUsername()));
        if (one != null && !one.getId().equals(worker.getId())) {
            throw new Exception("已存在的用户名");
        }
        worker.setPassword(bCryptPasswordEncoder.encode(worker.getPassword()));
        worker.setUpdateAt(DataTimeUtil.getNowTimeString());
        workerMapper.updateById(worker);
    }

    // 查询所有员工
    @Override
    public List<Worker> findAll() {
        return workerMapper.selectList(null);
    }

    @Override
    public PageResult<Worker> findByPage(Integer page, Integer size, String keyword) {
        LambdaQueryWrapper<Worker> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Worker::getUsername, keyword)
                    .or()
                    .like(Worker::getDepartment, keyword));
        }
        wrapper.orderByDesc(Worker::getCreateAt);
        Page<Worker> pageParam = new Page<>(page, size);
        Page<Worker> workerPage = workerMapper.selectPage(pageParam, wrapper);
        return new PageResult<>(
                workerPage.getTotal(),
                (int) workerPage.getCurrent(),
                (int) workerPage.getSize(),
                workerPage.getRecords()
        );
    }

    // 查询在职员工公开信息（用于前端展示）
    @Override
    public List<WorkerPublicVO> listPublicShowcase() {
        QueryWrapper<Worker> q = new QueryWrapper<>();
        q.eq("entry", true);
        return workerMapper.selectList(q).stream()
                .map(w -> new WorkerPublicVO(w.getId(), w.getNickname(), w.getGender(), w.getPhone(), w.getAvatar()))
                .collect(Collectors.toList());
    }

    // 根据ID查询员工
    @Override
    public Worker findById(String id) {
        return workerMapper.selectById(id);
    }

    // 根据ID删除员工及所有权限
    @Override
    public void deleteById(String id) {
        workerMapper.deleteById(id);
        //删除所有权限
        roleService.deleteWorkerAllRoles(id);
    }

}