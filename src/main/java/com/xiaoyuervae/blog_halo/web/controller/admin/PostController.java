package com.xiaoyuervae.blog_halo.web.controller.admin;

import com.xiaoyuervae.blog_halo.model.domain.*;
import com.xiaoyuervae.blog_halo.model.dto.HaloConst;
import com.xiaoyuervae.blog_halo.model.dto.LogsRecord;
import com.xiaoyuervae.blog_halo.service.CategoryService;
import com.xiaoyuervae.blog_halo.service.LogsService;
import com.xiaoyuervae.blog_halo.service.PostService;
import com.xiaoyuervae.blog_halo.service.TagService;
import com.xiaoyuervae.blog_halo.utils.HaloUtils;
import com.xiaoyuervae.blog_halo.web.controller.core.BaseController;
import com.xiaoyuervae.blog_halo.model.domain.*;
import com.xiaoyuervae.blog_halo.model.dto.HaloConst;
import com.xiaoyuervae.blog_halo.model.dto.LogsRecord;
import com.xiaoyuervae.blog_halo.service.CategoryService;
import com.xiaoyuervae.blog_halo.service.LogsService;
import com.xiaoyuervae.blog_halo.service.PostService;
import com.xiaoyuervae.blog_halo.service.TagService;
import com.xiaoyuervae.blog_halo.utils.HaloUtils;
import com.xiaoyuervae.blog_halo.web.controller.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author : xiaoyuervae
 * @date : 2017/12/10
 * @version : 1.0
 * description: 文章控制器
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/posts")
public class PostController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 处理后台获取文章列表的请求
     *
     * @param model model
     * @param page 当前页码
     * @param size 每页显示的条数
     * @return 模板路径admin/admin_post
     */
    @GetMapping
    public String posts(Model model,
                        @RequestParam(value = "status",defaultValue = "0") Integer status,
                        @RequestParam(value = "page",defaultValue = "0") Integer page,
                        @RequestParam(value = "size",defaultValue = "10") Integer size){
        Sort sort = new Sort(Sort.Direction.DESC,"postDate");
        Pageable pageable = PageRequest.of(page,size,sort);
        Page<Post> posts = postService.findPostByStatus(status, HaloConst.POST_TYPE_POST,pageable);
        model.addAttribute("posts",posts);
        model.addAttribute("publishCount",postService.findPostByStatus(0,HaloConst.POST_TYPE_POST,pageable).getTotalElements());
        model.addAttribute("draftCount",postService.findPostByStatus(1,HaloConst.POST_TYPE_POST,pageable).getTotalElements());
        model.addAttribute("trashCount",postService.findPostByStatus(2,HaloConst.POST_TYPE_POST,pageable).getTotalElements());
        model.addAttribute("status",status);
        return "admin/admin_post";
    }

    /**
     * 模糊查询文章
     *
     * @param model Model
     * @param keyword keyword 关键字
     * @param page page 当前页码
     * @param size size 每页显示条数
     * @return 模板路径admin/admin_post
     */
    @PostMapping(value="/search")
    public String searchPost(Model model,
                             @RequestParam(value = "keyword") String keyword,
                             @RequestParam(value = "page",defaultValue = "0") Integer page,
                             @RequestParam(value = "size",defaultValue = "10") Integer size){
        try {
            //排序规则
            Sort sort = new Sort(Sort.Direction.DESC,"postId");
            Pageable pageable = PageRequest.of(page,size,sort);
            model.addAttribute("posts",postService.searchPosts(keyword,pageable));
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
        }
        return "admin/admin_post";
    }

    /**
     * 处理预览文章的请求
     *
     * @param postId 文章编号
     * @param model model
     * @return 模板路径/themes/{theme}/post
     */
    @GetMapping(value = "/view")
    public String viewPost(@PathParam("postId") Long postId,Model model){
        Optional<Post> post = postService.findByPostId(postId);
        model.addAttribute("post",post.get());
        return this.render("post");
    }

    /**
     * 处理跳转到新建文章页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newPost(){
        return "admin/admin_post_md_editor";
    }

    /**
     * 去除html，htm后缀
     *
     * @param url url
     * @return String
     */
    private static String urlFilter(String url) {
        if (null != url) {
            final boolean urlEndsWithHtmlPostFix = url.endsWith(".html") || url.endsWith(".htm");
            if (urlEndsWithHtmlPostFix) {
                return url.substring(0, url.lastIndexOf("."));
            }
        }
        return url;
    }

    /**
     * 添加文章
     *
     * @param post Post实体
     * @param cateList 分类列表
     * @param tagList 标签列表
     * @param session session
     */
    @PostMapping(value = "/new/push")
    @ResponseBody
    public void pushPost(@ModelAttribute Post post, @RequestParam("cateList") List<String> cateList, @RequestParam("tagList") String tagList, HttpSession session){
        User user = (User)session.getAttribute(HaloConst.USER_SESSION_KEY);
        try{
            //提取摘要
            int postSummary = 50;
            if(StringUtils.isNotEmpty(HaloConst.OPTIONS.get("post_summary"))){
                postSummary = Integer.parseInt(HaloConst.OPTIONS.get("post_summary"));
            }
            String summaryText = HaloUtils.htmlToText(post.getPostContent());
            if(summaryText.length()>postSummary){
                String summary = HaloUtils.getSummary(post.getPostContent(), postSummary);
                post.setPostSummary(summary);
            }else{
                post.setPostSummary(summaryText);
            }
            if(null!=post.getPostId()){
                post.setPostDate(postService.findByPostId(post.getPostId()).get().getPostDate());
                post.setPostUpdate(new Date());
            }else{
                post.setPostDate(new Date());
                post.setPostUpdate(new Date());
            }
            post.setUser(user);
            List<Category> categories = categoryService.strListToCateList(cateList);
            post.setCategories(categories);
            if(StringUtils.isNotEmpty(tagList)){
                List<Tag> tags = tagService.strListToTagList(StringUtils.trim(tagList));
                post.setTags(tags);
            }
            post.setPostUrl(urlFilter(post.getPostUrl()));
            postService.saveByPost(post);
            logsService.saveByLogs(new Logs(LogsRecord.PUSH_POST,post.getPostTitle(),HaloUtils.getIpAddr(request),new Date()));
        }catch (Exception e){
            log.error("未知错误：", e.getMessage());
        }
    }


    /**
     * 处理移至回收站的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping("/throw")
    public String moveToTrash(@RequestParam("postId") Long postId){
        try{
            postService.updatePostStatus(postId,2);
            log.info("编号为"+postId+"的文章已被移到回收站");
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
        }
        return "redirect:/admin/posts";
    }

    /**
     * 处理文章为发布的状态
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping("/revert")
    public String moveToPublish(@RequestParam("postId") Long postId,
                                @RequestParam("status") Integer status){
        try{
            postService.updatePostStatus(postId,0);
            log.info("编号为"+postId+"的文章已改变为发布状态");
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
        }
        return "redirect:/admin/posts?status="+status;
    }

    /**
     * 处理删除文章的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/posts
     */
    @GetMapping(value = "/remove")
    public String removePost(@PathParam("postId") Long postId,@PathParam("postType") String postType){
        try{
            Optional<Post> post = postService.findByPostId(postId);
            postService.removeByPostId(postId);
            logsService.saveByLogs(new Logs(LogsRecord.REMOVE_POST,post.get().getPostTitle(),HaloUtils.getIpAddr(request),new Date()));
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
        }
        if(StringUtils.equals(HaloConst.POST_TYPE_POST,postType)){
            return "redirect:/admin/posts?status=2";
        }
        return "redirect:/admin/page";
    }

    /**
     * 跳转到编辑文章页面
     *
     * @param postId 文章编号
     * @param model model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editPost(@PathParam("postId") Long postId, Model model){
        Optional<Post> post = postService.findByPostId(postId);
        model.addAttribute("post",post.get());
        return "admin/admin_post_md_editor";
    }

    /**
     * 更新所有摘要
     *
     * @param postSummary 文章摘要字数
     * @return true：更新成功，false：更新失败
     */
    @GetMapping(value = "/updateSummary")
    @ResponseBody
    public boolean updateSummary(@PathParam("postSummary") Integer postSummary){
        try {
            postService.updateAllSummary(postSummary);
            return true;
        }catch (Exception e){
            log.error("未知错误：{0}",e.getMessage());
            return false;
        }
    }

    /**
     * 验证文章路径是否已经存在
     *
     * @param postUrl 文章路径
     * @return true：不存在，false：已存在
     */
    @GetMapping(value = "/checkUrl")
    @ResponseBody
    public boolean checkUrlExists(@PathParam("postUrl") String postUrl){
        postUrl = urlFilter(postUrl);
        Post post = postService.findByPostUrl(postUrl,HaloConst.POST_TYPE_POST);
        return null!=post;
    }

    /**
     * 将所有文章推送到百度
     *
     * @param baiduToken baiduToken
     * @return true or false
     */
    @GetMapping(value = "/pushAllToBaidu")
    @ResponseBody
    public boolean pushAllToBaidu(@PathParam("baiduToken") String baiduToken){
        if(StringUtils.isEmpty(baiduToken)){
            return false;
        }
        String blogUrl = HaloConst.OPTIONS.get("blog_url");
        List<Post> posts = postService.findAllPosts(HaloConst.POST_TYPE_POST);
        StringBuilder urls = new StringBuilder();
        for(Post post:posts){
            urls.append(blogUrl);
            urls.append("/archives/");
            urls.append(post.getPostUrl());
            urls.append("\n");
        }
        String result = HaloUtils.baiduPost(blogUrl, baiduToken, urls.toString());
        if (StringUtils.isEmpty(result)) {
            return false;
        }
        return true;
    }
}
