package br.com.on.app.repository;

import br.com.on.app.entity.PessoaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<PessoaEntity, Integer> {

    Optional<PessoaEntity> findByCpf(String cpf);

}
