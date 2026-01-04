package com.mch.hub.config;

import com.mch.hub.domain.CommitEntity;
import com.mch.hub.domain.OrganizationEntity;
import com.mch.hub.domain.RepositoryEntity;
import com.mch.hub.domain.UserEntity;
import com.mch.hub.repository.CommitRepository;
import com.mch.hub.repository.OrganizationRepository;
import com.mch.hub.repository.RepositoryRepository;
import com.mch.hub.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataLoader {
    @Bean
    CommandLineRunner seedData(UserRepository users, OrganizationRepository orgs, RepositoryRepository repos, CommitRepository commits) {
        return args -> {
            if (!users.findAll().isEmpty()) {
                return;
            }

            UserEntity alice = new UserEntity();
            alice.setUsername("alice");
            alice.setDisplayName("Alice Explorer");
            alice.setEmail("alice@example.com");
            users.save(alice);

            UserEntity bob = new UserEntity();
            bob.setUsername("bob");
            bob.setDisplayName("Bob Crafter");
            bob.setEmail("bob@example.com");
            users.save(bob);

            OrganizationEntity builders = new OrganizationEntity();
            builders.setSlug("builders-guild");
            builders.setDisplayName("Builders Guild");
            builders.setDescription("Community building world timelines.");
            builders.setMembers(Set.of(alice, bob));
            orgs.save(builders);

            RepositoryEntity soloRepo = new RepositoryEntity();
            soloRepo.setName("solo-world");
            soloRepo.setDescription("Alice's personal history of her base.");
            soloRepo.setOwnerUser(alice);
            soloRepo.setStoragePath("/data/repos/alice/solo-world");
            repos.save(soloRepo);

            RepositoryEntity guildRepo = new RepositoryEntity();
            guildRepo.setName("guild-hall");
            guildRepo.setDescription("Shared guild hall world timeline.");
            guildRepo.setOwnerOrganization(builders);
            guildRepo.setStoragePath("/data/repos/builders-guild/guild-hall");
            repos.save(guildRepo);

            List<RepositoryEntity> targets = List.of(soloRepo, guildRepo);
            for (RepositoryEntity r : targets) {
                for (int i = 0; i < 3; i++) {
                    CommitEntity c = new CommitEntity();
                    c.setRepository(r);
                    c.setHash(r.getName().substring(0, Math.min(4, r.getName().length())) + "-" + (i + 1));
                    c.setMessage("Snapshot " + (i + 1));
                    c.setCommittedAt(OffsetDateTime.now().minusDays(2 - i));
                    commits.save(c);
                }
            }
        };
    }
}
