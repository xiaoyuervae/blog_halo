package com.xiaoyuervae.blog_halo.config;

import com.xiaoyuervae.blog_halo.model.domain.Attachment;
import com.xiaoyuervae.blog_halo.model.dto.HaloConst;
import com.xiaoyuervae.blog_halo.model.dto.Theme;
import com.xiaoyuervae.blog_halo.service.AttachmentService;
import com.xiaoyuervae.blog_halo.service.OptionsService;
import com.xiaoyuervae.blog_halo.utils.HaloUtils;
import com.xiaoyuervae.blog_halo.web.controller.core.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.List;
import java.util.Map;

/**
 * @author : xiaoyuervae
 * @version : 1.0
 * @date : 2017/12/22
 */
@Slf4j
@Configuration
public class StartupConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private OptionsService optionsService;

    @Autowired
    private AttachmentService attachmentService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.loadActiveTheme();
        this.loadOptions();
        this.loadFiles();
        this.loadThemes();
    }

    /**
     * 加载主题设置
     */
    private void loadActiveTheme() {
        try {
            String themeValue = optionsService.findOneOption("theme");
            if (StringUtils.isNotEmpty("themeValue")) {
                BaseController.THEME = themeValue;
            }
        } catch (Exception e) {
            log.error("加载主题设置失败：{0}", e.getMessage());
        }
    }

    /**
     * 加载设置选项
     */
    private void loadOptions() {
        try {
            Map<String, String> options = optionsService.findAllOptions();
            if (options != null && !options.isEmpty()) {
                HaloConst.OPTIONS = options;
            }
        } catch (Exception e) {
            log.error("加载设置选项失败：{0}", e.getMessage());
        }
    }

    /**
     * 加载所有文件
     */
    private void loadFiles() {
        try {
            List<Attachment> attachments = attachmentService.findAllAttachments();
            if (null != attachments) {
                HaloConst.ATTACHMENTS = attachments;
            }
        } catch (Exception e) {
            log.error("加载所有文件失败：{0}", e.getMessage());
        }
    }

    /**
     * 加载所有主题
     */
    private void loadThemes() {
        try {
            HaloConst.THEMES.clear();
            List<Theme> themes = HaloUtils.getThemes();
            if (null != themes) {
                HaloConst.THEMES = themes;
            }
        } catch (Exception e) {
            log.error("加载主题失败：{0}", e.getMessage());
        }
    }
}
