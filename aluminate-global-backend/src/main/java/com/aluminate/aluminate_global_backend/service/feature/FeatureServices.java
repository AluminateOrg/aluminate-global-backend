package com.aluminate.aluminate_global_backend.service.feature;

import com.aluminate.aluminate_global_backend.dto.features.FeatureCreateReq;
import com.aluminate.aluminate_global_backend.dto.features.FeatureResp;
import com.aluminate.aluminate_global_backend.model.Features;
import com.aluminate.aluminate_global_backend.repository.FeaturesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeatureServices {

    private final FeaturesRepository featuresRepository;

    public FeatureServices(FeaturesRepository featuresRepository) {
        this.featuresRepository = featuresRepository;
    }


    public FeatureResp createFeature(FeatureCreateReq featureCreateReq) {
        if (featureCreateReq == null) throw new IllegalArgumentException("FeatureCreateReq cannot be null");
        if (featureCreateReq.getName() == null || featureCreateReq.getName().isBlank()) throw new IllegalArgumentException("Feature name cannot be null or blank");
        Features features = new Features();
        features.setName(featureCreateReq.getName());
        featuresRepository.save(features);

        return (FeatureResp) featuresRepository.findAll()
                .stream()
                .map(this::toResp)
                .collect(Collectors.toList());
    }

    private FeatureResp toResp(Features f) {
        FeatureResp resp = new FeatureResp();
        resp.setId(f.getId());
        resp.setName(f.getName());
        return resp;
    }

    public List<FeatureResp> getAllFeatures() {
        return featuresRepository.findAll()
                .stream()
                .map(this::toResp)
                .collect(Collectors.toList());
    }
}

