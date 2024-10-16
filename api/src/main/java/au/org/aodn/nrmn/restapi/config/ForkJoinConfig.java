package au.org.aodn.nrmn.restapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ForkJoinConfig {

    protected static Logger logger = LoggerFactory.getLogger(ForkJoinConfig.class);

    static {
        // By default, ForkJoinPool set parallelism to core - 1, therefore 2 core still use 1 thread, we want to change
        // this default utilize the resource better.
        String coreNum = String.valueOf(Runtime.getRuntime().availableProcessors());
        System.setProperty(
                "java.util.concurrent.ForkJoinPool.common.parallelism",
                coreNum
        );
        logger.info("Set ForkJoin use number of cores = {}", coreNum);
    }
}
