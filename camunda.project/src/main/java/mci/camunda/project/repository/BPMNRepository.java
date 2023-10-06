package mci.camunda.project.repository;

import mci.camunda.project.Assigner.BPMN.BPMN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPMNRepository extends JpaRepository<BPMN, Integer> {
}
