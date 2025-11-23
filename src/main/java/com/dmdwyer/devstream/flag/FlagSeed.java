package com.dmdwyer.devstream.flag;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dmdwyer.devstream.entity.Flag;
import com.dmdwyer.devstream.repository.FlagRepository;

@Configuration
public class FlagSeed {
  @Bean
  CommandLineRunner seed(FlagRepository repo) {
    return args -> {
      if (repo.count() == 0) {
        Flag f = new Flag();
        f.setFlagKey("homepage_banner");
        f.setEnabled(true);
        f.setVariantsJson("{\"A\":50,\"B\":50}");
        f.setRulesJson("[{\"if\":\"country=IE\",\"then\":\"A\"},{\"if\":\"plan=premium\",\"then\":\"B\"}]");
        repo.save(f);
      }
    };
  }
}
