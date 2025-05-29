package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.Meta;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO handleGetCompany(Specification spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        Meta mt = new Meta();
        ResultPaginationDTO rs = new ResultPaginationDTO();
        mt.setPage(pageCompany.getNumber() + 1);
        mt.setPageSize(pageCompany.getSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;

    }

    public Company getUserById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if (company.isPresent()) {
            return company.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company company) {
        Company currentCompany = getUserById(company.getId());
        if (currentCompany != null) {
            currentCompany.setName(company.getName());

            currentCompany.setDescription(company.getDescription());

            currentCompany.setAddress(company.getAddress());

            currentCompany.setLogo(company.getLogo());

            currentCompany.setCreatedAt(company.getCreatedAt());

            currentCompany.setUpdatedAt(company.getUpdatedAt());

            currentCompany.setCreatedBy(company.getCreatedBy());

            currentCompany.setUpdateBy(company.getUpdateBy());
            this.companyRepository.save(currentCompany);
        }
        return currentCompany;
    }

    public void hanldeDeleteCompanyById(long id) {
        this.companyRepository.deleteById(id);
    }

}
