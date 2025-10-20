package com.aluminate.aluminate_global_backend.controller;


import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.getInfo.InfoResponse;
import com.aluminate.aluminate_global_backend.service.auth.AuthService;
import com.aluminate.aluminate_global_backend.service.info.infoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/admin/info")
public class InfoController {

    private final Logger logger =  LoggerFactory.getLogger(InfoController.class);
    private final infoService infoService;

    public InfoController( infoService infoService) {
        this.infoService = infoService;
    }

    @GetMapping("/getUser")
    public ResponseEntity<ResponseWrapper<InfoResponse>> getUser() {

        InfoResponse infoResponse = infoService.getUser();
        ResponseWrapper<InfoResponse> body = new ResponseWrapper<>(
                true,
                "User retrieval successful",
                infoResponse
        );

        return ResponseEntity.ok(body);
    }


}
