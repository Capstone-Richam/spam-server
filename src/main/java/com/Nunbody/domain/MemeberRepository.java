package com.Nunbody.domain;

import com.Nunbody.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemeberRepository extends JpaRepository<Member,Long> {
}
