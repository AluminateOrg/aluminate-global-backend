package com.aluminate.aluminate_global_backend.controller.subscription;

import com.aluminate.aluminate_global_backend.config.ResponseWrapper;
import com.aluminate.aluminate_global_backend.dto.features.FeatureCreateReq;
import com.aluminate.aluminate_global_backend.dto.features.FeatureResp;
import com.aluminate.aluminate_global_backend.service.feature.FeatureServices;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/superAdmin/feature")
public class FeatureController {

    private final FeatureServices featureServices;

    public FeatureController(FeatureServices featureServices) {
        this.featureServices = featureServices;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<FeatureResp>> createFeature(FeatureCreateReq featureCreateReq) {
        try {
            FeatureResp featureResp = featureServices.createFeature(featureCreateReq);
            if (featureResp == null) {
                return new ResponseEntity<>(new ResponseWrapper<>(false, "Feature creation failed", null), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ResponseWrapper<>(true, "Feature created successfully", featureResp),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<FeatureResp>>> getAllFeatures() {
        try {
            List<FeatureResp> features = featureServices.getAllFeatures();
            if (features == null) {
                return new ResponseEntity<>(new ResponseWrapper<>(false, "No features found", null), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ResponseWrapper<>(true, "Features retrieved successfully", features),
                        HttpStatus.OK);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
