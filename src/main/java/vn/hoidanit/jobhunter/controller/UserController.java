package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createNewUser(@RequestBody User postManUser) {
        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User ericUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ericUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        // nếu id lớn hơn 1500 thì nhảy lên hàm handleException để tra về mã lỗi và
        // cảnh bảo bạn muốn gửi
        if (id >= 1500) {
            throw new IdInvalidException("id không được lớn hơn 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok("ericUser");
    }

    @GetMapping("/users/{user-id}")
    public ResponseEntity<User> getUserByid(@PathVariable("user-id") long id) {
        User fetchUser = this.userService.fetchUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(fetchUser);
    }

    @GetMapping("/users")
    // current : số trang đang đứng
    // pageSize số lượng phần tử muốn hiển thị
    // filler hỗ trợ tìm kiếm
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    // @GetMapping("/")
    // public ResponseEntity<List<User>> getPage() {
    // return
    // ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser());
    // }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(user));
    }
}
