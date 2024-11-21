package com.cookie.domain.actor.repository;

import com.cookie.domain.actor.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, Long> {
}
