package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateSkillDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateSkillDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class SkillConrtroller {

    private final SkillRepository skillRepository;
    private final SkillService skillService;

    public SkillConrtroller(SkillService skillService, SkillRepository skillRepository) {
        this.skillService = skillService;
        this.skillRepository = skillRepository;
    }

    @PostMapping("/skills")
    @ApiMessage("create a new skill")
    public ResponseEntity<ResCreateSkillDTO> create(@Valid @RequestBody Skill reqSkill)
            throws IdInvalidException {
        boolean isNameExits = this.skillService.isNameExist(reqSkill.getName());
        if (isNameExits) {
            throw new IdInvalidException(
                    "name  " + reqSkill.getName() + " đã tồn tại, vui lòng sử dụng name khác.");
        }

        Skill curentSkill = this.skillService.handleCreateSkill(reqSkill);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.skillService.convertToResCreateSkillDTO(curentSkill));
    }

    @PutMapping("/skills")
    @ApiMessage("Update a skill")
    public ResponseEntity<ResUpdateSkillDTO> update(@RequestBody Skill reqSkill) throws IdInvalidException {
        Skill currenSkill = this.skillService.handleUpdateSkill(reqSkill);
        if (currenSkill == null) {
            throw new IdInvalidException("Skill với id = " + reqSkill.getId() + " không tồn tại");
        }
        boolean isNameExits = this.skillService.isNameExist(reqSkill.getName());
        if (isNameExits) {
            throw new IdInvalidException(
                    "name  " + reqSkill.getName() + " đã tồn tại, vui lòng sử dụng name khác.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.convertToUpdateSkillDTO(currenSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skills")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("skill với id = " + id + " không tồn tại");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/skills")
    @ApiMessage("fetch all skills")
    public ResponseEntity<ResultPaginationDTO> getAll(@Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkills(spec, pageable));
    }

}
