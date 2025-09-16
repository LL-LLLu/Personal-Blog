package com.luqi.weblog.admin.model.vo.blogsettings;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author: 犬小哈
 * @url: www.quanxiaoha.com
 * @date: 2023-09-15 14:07
 * @description: 博客基础信息修改
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(value = "Blog Basic Information Update VO")
public class UpdateBlogSettingsReqVO {

    @NotBlank(message = "Blog LOGO cannot be empty")
    private String logo;

    @NotBlank(message = "Blog name cannot be empty")
    private String name;

    @NotBlank(message = "Blog author cannot be empty")
    private String author;

    @NotBlank(message = "Blog introduction cannot be empty")
    private String introduction;

    @NotBlank(message = "Blog avatar cannot be empty")
    private String avatar;

    private String githubHomepage;

    private String csdnHomepage;

    private String giteeHomepage;

    private String zhihuHomepage;
}