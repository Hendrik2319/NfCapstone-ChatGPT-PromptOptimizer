package net.schwarzbaer.spring.promptoptimizer.backend.security.repositories;

import net.schwarzbaer.spring.promptoptimizer.backend.security.models.StoredUserInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StoredUserInfoRepository extends MongoRepository<StoredUserInfo, String> {
}
