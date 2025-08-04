package com.aluminate.aluminate_global_backend.controller.superAdmin;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.getInfo.SuperAdminDTO;
import com.aluminate.aluminate_global_backend.service.info.infoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/superAdmin/info")
public class infoSuperController {

    // to handle super admin specific info retrieval or management.
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final infoService infoService;
    public infoSuperController(infoService infoService) {
        this.infoService = infoService;
    }
    @GetMapping("/getAdminInfo")
    public ResponseEntity<ResponseWrapper<SuperAdminDTO>> getAdminInfo() {
        SuperAdminDTO superAdminDTO = infoService.getAdminInfo();
        ResponseWrapper<SuperAdminDTO> body = new ResponseWrapper<>(
                true,
                "Super Admin info retrieval successful",
                superAdminDTO
        );
        return ResponseEntity.ok(body);
    }

}
