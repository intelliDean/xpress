package com.monie.xpress.auth_config.user.data.repositories;

import com.monie.xpress.auth_config.user.data.models.XpressToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XpressTokenRepository extends JpaRepository<XpressToken, Long> {

}
