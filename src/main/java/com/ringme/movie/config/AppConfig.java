package com.ringme.movie.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppConfig {
    @Value("${base.path.cdn}")
    private String basePathCdn;

    @Value("${base-url}")
    private String baseUrl;

    @Value("${generate-sub-cmd-srt}")
    private String generateSubCmdSrt;

    @Value("${generate-sub-cmd-vtt}")
    private String generateSubCmdVtt;

    @Value("${add-sub-cmd-in-m3u8}")
    private String addSubCmdInM3u8;

    @Value("${edit-sub-profile-480}")
    private String editSubProfile480;

    @Value("${edit-sub-profile-720}")
    private String editSubProfile720;

    @Value("${edit-sub-profile-240}")
    private String editSubProfile240;

    @Value("${edit-sub-profile-360}")
    private String editSubProfile360;
}
