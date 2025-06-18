package vn.hoidanit.jobhunter.controller;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;
    private final JobRepository jobRepository;

    public JobController(JobService jobService, JobRepository jobRepository) {
        this.jobService = jobService;
        this.jobRepository = jobRepository;
    }

    @PostMapping("/jobs")
    @ApiMessage("create new job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job)
            throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("update new job")
    public ResponseEntity<ResUpdateJobDTO> update(@Valid @RequestBody Job job)
            throws IdInvalidException {

        Job currentJob = this.jobService.fetchJobById(job.getId());

        if (currentJob == null) {
            throw new IdInvalidException("job not found");
        }
        return ResponseEntity.ok().body(this.jobService.update(job));

    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("delete a job by id")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        Job currenJob = this.jobService.fetchJobById(id);
        if (currenJob == null) {
            throw new IdInvalidException("job not found");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok(null);

    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("get a job by id")
    public ResponseEntity<Job> getJob(@PathVariable("id") long id) throws IdInvalidException {
        Job currenJob = this.jobService.fetchJobById(id);
        if (currenJob == null) {
            throw new IdInvalidException("job not found");
        }

        return ResponseEntity.ok(currenJob);
    }

    @GetMapping("/jobs")
    @ApiMessage("fetch all job")
    public ResponseEntity<ResultPaginationDTO> getAllJob(@Filter Specification<Skill> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAll(spec, pageable));
    }
}
