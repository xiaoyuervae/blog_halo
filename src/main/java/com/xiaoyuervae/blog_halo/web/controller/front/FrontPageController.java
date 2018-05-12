package com.xiaoyuervae.blog_halo.web.controller.front;

import com.xiaoyuervae.blog_halo.model.domain.Comment;
import com.xiaoyuervae.blog_halo.model.domain.Gallery;
import com.xiaoyuervae.blog_halo.model.domain.Post;
import com.xiaoyuervae.blog_halo.model.dto.HaloConst;
import com.xiaoyuervae.blog_halo.service.CommentService;
import com.xiaoyuervae.blog_halo.service.GalleryService;
import com.xiaoyuervae.blog_halo.service.PostService;
import com.xiaoyuervae.blog_halo.web.controller.core.BaseController;
import com.xiaoyuervae.blog_halo.model.domain.Comment;
import com.xiaoyuervae.blog_halo.model.domain.Gallery;
import com.xiaoyuervae.blog_halo.model.domain.Post;
import com.xiaoyuervae.blog_halo.service.CommentService;
import com.xiaoyuervae.blog_halo.service.GalleryService;
import com.xiaoyuervae.blog_halo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author : xiaoyuervae
 * @version : 1.0
 * @date : 2018/4/26
 */
@Controller
public class FrontPageController extends BaseController {

    @Autowired
    private GalleryService galleryService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    /**
     * 跳转到图库页面
     *
     * @return 模板路径/themes/{theme}/gallery
     */
    @GetMapping(value = "/gallery")
    public String gallery(Model model) {
        List<Gallery> galleries = galleryService.findAllGalleries();
        model.addAttribute("galleries", galleries);
        return this.render("gallery");
    }

    /**
     * 友情链接
     *
     * @return 模板路径/themes/{theme}/links
     */
    @GetMapping(value = "/links")
    public String links() {
        return this.render("links");
    }

    /**
     * 渲染自定义页面
     *
     * @param postUrl 页面路径
     * @param model   model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = "/p/{postUrl}")
    public String getPage(@PathVariable(value = "postUrl") String postUrl, Model model) {
        Post post = postService.findByPostUrl(postUrl, HaloConst.POST_TYPE_PAGE);

        Sort sort = new Sort(Sort.Direction.DESC,"commentDate");
        Pageable pageable = PageRequest.of(0,999,sort);
        Page<Comment> comments = commentService.findCommentsByPostAndCommentStatus(post,pageable,2);

        model.addAttribute("comments",comments);
        model.addAttribute("post", post);
        return this.render("page");
    }
}
