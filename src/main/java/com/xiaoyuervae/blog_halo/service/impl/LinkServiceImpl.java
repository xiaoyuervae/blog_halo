package com.xiaoyuervae.blog_halo.service.impl;

import com.xiaoyuervae.blog_halo.model.domain.Link;
import com.xiaoyuervae.blog_halo.repository.LinkRepository;
import com.xiaoyuervae.blog_halo.service.LinkService;
import com.xiaoyuervae.blog_halo.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author : xiaoyuervae
 * @version : 1.0
 * @date : 2017/11/14
 */
@Service
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkRepository linkRepository;

    /**
     * 新增/修改友情链接
     *
     * @param link link
     * @return Link
     */
    @Override
    public Link saveByLink(Link link) {
        return linkRepository.save(link);
    }

    /**
     * 移除友情链接
     *
     * @param linkId linkId
     * @return link
     */
    @Override
    public Link removeByLinkId(Long linkId) {
        Optional<Link> link = this.findByLinkId(linkId);
        linkRepository.delete(link.get());
        return link.get();
    }

    /**
     * 查询所有友情链接
     *
     * @return list
     */
    @Override
    public List<Link> findAllLinks() {
        return linkRepository.findAll();
    }

    /**
     * 根据编号查询友情链接
     *
     * @param linkId linkId
     * @return Link
     */
    @Override
    public Optional<Link> findByLinkId(Long linkId) {
        return linkRepository.findById(linkId);
    }
}
