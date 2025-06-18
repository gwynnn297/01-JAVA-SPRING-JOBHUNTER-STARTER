package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skill;

import vn.hoidanit.jobhunter.domain.response.ResCreateSkillDTO;
import vn.hoidanit.jobhunter.domain.response.ResSkillDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateSkillDTO;

import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;

    }

    public boolean isNameExist(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill handleCreateSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public ResCreateSkillDTO convertToResCreateSkillDTO(Skill skill) {
        ResCreateSkillDTO res = new ResCreateSkillDTO();
        res.setId(skill.getId());
        res.setName(skill.getName());
        res.setCreatedAt(skill.getCreatedAt());
        res.setCreatedBy(skill.getCreatedBy());
        res.setUpdatedAt(skill.getUpdatedAt());
        res.setUpdatedBy(skill.getUpdatedBy());
        return res;
    }

    public Skill handleUpdateSkill(Skill skill) {
        Optional<Skill> skillOptional = this.skillRepository.findById(skill.getId());

        if (skillOptional.isPresent()) {
            Skill currentSkill = skillOptional.get();
            currentSkill.setName(skill.getName());
            currentSkill.setCreatedAt(skill.getCreatedAt());
            currentSkill.setCreatedBy(skill.getCreatedBy());
            currentSkill.setUpdatedAt(skill.getUpdatedAt());
            currentSkill.setUpdatedBy(skill.getUpdatedBy());

            return this.skillRepository.save(currentSkill);
        }

        return null;
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> curenSkill = this.skillRepository.findById(id);
        if (curenSkill.isPresent()) {
            return curenSkill.get();
        }
        return null;
    }

    public ResUpdateSkillDTO convertToUpdateSkillDTO(Skill skill) {
        ResUpdateSkillDTO res = new ResUpdateSkillDTO();
        res.setId(skill.getId());
        res.setName(skill.getName());
        res.setCreatedAt(skill.getCreatedAt());
        res.setCreatedBy(skill.getCreatedBy());
        res.setUpdatedAt(skill.getUpdatedAt());
        res.setUpdatedBy(skill.getUpdatedBy());

        return res;
    }

    public ResultPaginationDTO fetchAllSkills(Specification spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());

        rs.setMeta(mt);
        List<ResSkillDTO> listSkill = pageSkill.getContent()
                .stream().map(item -> new ResSkillDTO(
                        item.getId(),
                        item.getName(),
                        item.getCreatedAt(),
                        item.getUpdatedAt(),
                        item.getCreatedBy(),
                        item.getUpdatedBy()))
                .collect(Collectors.toList());

        rs.setResult(listSkill);
        return rs;

    }

    public void deleteSkill(long id) {
        // delete job (inside job_skill table)
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill curentSkill = skillOptional.get();
        // xóa đi những skill nằm trong các job đó

        curentSkill.getJobs().forEach(job -> job.getSkills().remove(curentSkill));

        // delete skill
        this.skillRepository.delete(curentSkill);
    }
}