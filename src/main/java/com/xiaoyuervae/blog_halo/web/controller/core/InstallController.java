package com.xiaoyuervae.blog_halo.web.controller.core;

import com.xiaoyuervae.blog_halo.model.domain.*;
import com.xiaoyuervae.blog_halo.model.dto.HaloConst;
import com.xiaoyuervae.blog_halo.model.dto.LogsRecord;
import com.xiaoyuervae.blog_halo.service.*;
import com.xiaoyuervae.blog_halo.utils.HaloUtils;
import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : xiaoyuervae
 * @version : 1.0
 * description : 安装控制器
 * @date : 2018/1/28
 */
@Slf4j
@Controller
@RequestMapping(value = "/install")
public class InstallController {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogsService logsService;

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private Configuration configuration;

    /**
     * 渲染安装页面
     *
     * @param model model
     * @return 模板路径
     */
    @GetMapping
    public String install(Model model) {
        try {
            if (StringUtils.equals("true", HaloConst.OPTIONS.get("is_install"))) {
                model.addAttribute("isInstall", true);
            } else {
                model.addAttribute("isInstall", false);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "common/install";
    }

    /**
     * 执行安装
     *
     * @param siteTitle       博客标题
     * @param siteUrl         博客网址
     * @param userName        用户名
     * @param userDisplayName 用户名显示名
     * @param userEmail       用户邮箱
     * @param userPwd         用户密码
     * @param request         request
     * @return true：安装成功，false：安装失败
     */
    @PostMapping(value = "/do")
    @ResponseBody
    public boolean doInstall(@RequestParam("blogTitle") String blogTitle,
                             @RequestParam("blogUrl") String blogUrl,
                             @RequestParam("userName") String userName,
                             @RequestParam("userDisplayName") String userDisplayName,
                             @RequestParam("userEmail") String userEmail,
                             @RequestParam("userPwd") String userPwd,
                             HttpServletRequest request) {
        try {
            if (StringUtils.equals("true", HaloConst.OPTIONS.get("is_install"))) {
                return false;
            }
            //创建新的用户
            User user = new User();
            user.setUserName(userName);
            if (StringUtils.isBlank(userDisplayName)) {
                userDisplayName = userName;
            }
            user.setUserDisplayName(userDisplayName);
            user.setUserEmail(userEmail);
            user.setUserPass(HaloUtils.getMD5(userPwd));
            userService.saveByUser(user);

            //默认分类
            Category category = new Category();
            category.setCateName("未分类");
            category.setCateUrl("default");
            category.setCateDesc("未分类");
            categoryService.saveByCategory(category);

            //第一篇文章
            Post post = new Post();
            List<Category> categories = new ArrayList<>();
            categories.add(category);
            post.setPostTitle("Hello blog_halo!");
            post.setPostContentMd("#Hello blog_halo!\n" +
                    "欢迎使用blog_halo进行创作，删除这篇文章后赶紧开始吧。");
            post.setPostContent("<h1 id=\"h1-hello-blog_halo-\"><a name=\"Hello blog_halo!\" class=\"reference-link\"></a><span class=\"header-link octicon octicon-link\"></span>Hello blog_halo!</h1><p>欢迎使用blog_halo进行创作，删除这篇文章后赶紧开始吧。</p>\n");
            post.setPostSummary("欢迎使用blog_halo进行创作，删除这篇文章后赶紧开始吧。");
            post.setPostStatus(0);
            post.setPostDate(new Date());
            post.setPostUrl("hello-blog_halo");
            post.setUser(user);
            post.setCategories(categories);
            postService.saveByPost(post);

            //第一个评论
            Comment comment = new Comment();
            comment.setPost(post);
            comment.setCommentAuthor("ruibaby");
            comment.setCommentAuthorEmail("i@xiaoyuervae.com");
            comment.setCommentAuthorUrl("https://xiaoyuervae.com");
            comment.setCommentAuthorIp("127.0.0.1");
            comment.setCommentAuthorAvatarMd5("7com7f29278071bd4dce995612d428834");
            comment.setCommentDate(new Date());
            comment.setCommentContent("欢迎，欢迎！");
            comment.setCommentStatus(0);
            comment.setCommentAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.162 Safari/537.36");
            comment.setIsAdmin(0);
            commentService.saveByComment(comment);

            optionsService.saveOption("is_install", "true");

            //保存博客标题和博客地址设置
            optionsService.saveOption("blog_title", blogTitle);
            optionsService.saveOption("blog_url", blogUrl);

            //设置默认主题
            optionsService.saveOption("theme", "anatole");

            //建立网站时间
            optionsService.saveOption("blog_start", HaloUtils.getStringDate("yyyy-MM-dd"));

            //默认评论系统
            optionsService.saveOption("comment_system", "native");

            //默认不配置邮件系统
            optionsService.saveOption("smtp_email_enable", "false");

            //新评论，审核通过，回复，默认不通知
            optionsService.saveOption("new_comment_notice", "false");
            optionsService.saveOption("comment_pass_notice", "false");
            optionsService.saveOption("comment_reply_notice", "false");

            //更新日志
            logsService.saveByLogs(
                    new Logs(
                            LogsRecord.INSTALL,
                            "安装成功，欢迎使用blog_halo。",
                            HaloUtils.getIpAddr(request),
                            new Date()
                    )
            );

            Menu menuIndex = new Menu();
            menuIndex.setMenuName("首页");
            menuIndex.setMenuUrl("/");
            menuIndex.setMenuSort(1);
            menuIndex.setMenuIcon("");
            menuService.saveByMenu(menuIndex);

            Menu menuArchive = new Menu();
            menuArchive.setMenuName("归档");
            menuArchive.setMenuUrl("/archives");
            menuArchive.setMenuSort(2);
            menuArchive.setMenuIcon("");
            menuService.saveByMenu(menuArchive);

            HaloConst.OPTIONS.clear();
            HaloConst.OPTIONS = optionsService.findAllOptions();

            configuration.setSharedVariable("options", optionsService.findAllOptions());
            configuration.setSharedVariable("user", userService.findUser());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}
