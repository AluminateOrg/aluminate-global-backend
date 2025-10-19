package com.aluminate.aluminate_global_backend.service.dockerSeervice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DockerService {

    public void createOrgContainer(String orgId) throws IOException, InterruptedException {
        // The docker-compose command
        String command = String.format(
                "ORG_SLUG=%s docker compose -f org-template/docker-compose-org.yml -p %s_org up --build -d",
                orgId, orgId
        );

        // Use bash -c so environment variables and shell syntax work
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);

        // Working directory = project root
        processBuilder.directory(new java.io.File(System.getProperty("user.dir"))); // 👈 project root
        processBuilder.redirectErrorStream(true);

        // Start the process
        Process process = processBuilder.start();

        // Capture and print Docker CLI output
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[DOCKER] " + line);
            }
        }

        // Wait for process completion
        int exitCode = process.waitFor();
        if (exitCode == 0) {
            System.out.println("✅ Container created successfully for ORG_SLUG=" + orgId);
        } else {
            throw new RuntimeException("❌ Docker compose failed. Exit code: " + exitCode);
        }
    }
}
