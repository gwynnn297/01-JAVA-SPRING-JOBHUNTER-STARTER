package vn.hoidanit.jobhunter.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO.userGetAccount;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @Value("${hoidanit.jwt.refresh-token-validity-in-second}")
        private long refreshTokenExpriration;

        // login vào thì cần có token, client cần gửi lên token này để xác minh(xác
        // thực)
        @PostMapping("/auth/login")
        // Valid hỗ trợ viết validation trong model
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {

                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getUsername(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername để gì đè
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);
                // set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này )
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResLoginDTO res = new ResLoginDTO();
                // lấy data thực trả ra khi người dùng login
                User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getName(),
                                        currentUserDB.getEmail());
                        res.setUser(userLogin);
                }

                String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
                res.setAccessToken(access_token);

                // create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

                // update refesh token
                this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

                // set cookie
                ResponseCookie resCookie = ResponseCookie
                                .from("refresh_token", refresh_token)
                                .httpOnly(true) // Chỉ cho server truy cập, chặn truy cập từ JavaScript (chống XSS)
                                .secure(true) // Chỉ gửi cookie qua HTTPS (bảo mật khi truyền)
                                .path("/") // Cookie có hiệu lực trên toàn bộ domain
                                .maxAge(refreshTokenExpriration) // Thời gian sống của cookie là 100 ngày theo phía
                                                                 // setup ở propertié
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                                .body(res);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResLoginDTO.userGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";

                User currentUserDB = this.userService.handleGetUserByUsername(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                ResLoginDTO.userGetAccount userAccount = new ResLoginDTO.userGetAccount();
                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getName());
                        userLogin.setName(currentUserDB.getEmail());
                        userAccount.setUser(userLogin);
                }

                return ResponseEntity.ok().body(userAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("get user by refresh token")
        // lấy refresh token từ cookie và tiến hành decode
        public ResponseEntity<ResLoginDTO> getRefreshToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("abc")) {
                        throw new IdInvalidException("bạn không có refresh token ở cookies");
                }
                Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
                // decodedToken lấy ra được refresh token và tiến hành đọc claim trong đó lấy ra
                // email đăng nhập
                String email = decodedToken.getSubject();

                // check token and email có tồn tại không
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("refresh token không hợp lệ");
                }
                ResLoginDTO res = new ResLoginDTO();
                // lấy data thực trả ra khi người dùng login
                User currentUserDB = this.userService.handleGetUserByUsername(email);
                if (currentUserDB != null) {
                        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getName(),
                                        currentUserDB.getEmail());
                        res.setUser(userLogin);
                }

                String access_token = this.securityUtil.createAccessToken(email, res.getUser());
                res.setAccessToken(access_token);

                // create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

                // update refesh token
                this.userService.updateUserToken(refresh_token, email);

                // set cookie
                ResponseCookie resCookie = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true) // Chỉ cho server truy cập, chặn truy cập từ JavaScript (chống XSS)
                                .secure(true) // Chỉ gửi cookie qua HTTPS (bảo mật khi truyền)
                                .path("/") // Cookie có hiệu lực trên toàn bộ domain
                                .maxAge(refreshTokenExpriration) // Thời gian sống của cookie là 100 ngày theo phía
                                                                 // setup ở propertié
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, resCookie.toString())
                                .body(res);
        }

        // login out API
        @GetMapping("/auth/logout")
        @ApiMessage("logout user")
        public ResponseEntity<Void> logout() throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("Acces token không hợp lệ");
                }
                this.userService.updateUserToken(null, email);
                ResponseCookie deleteSpringCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true) // Chỉ cho server truy cập, chặn truy cập từ JavaScript (chống XSS)
                                .secure(true) // Chỉ gửi cookie qua HTTPS (bảo mật khi truyền)
                                .path("/") // Cookie có hiệu lực trên toàn bộ domain
                                .maxAge(0) // Thời gian sống của cookie là 100 ngày theo phía
                                           // setup ở propertié
                                .build();

                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                                .body(null);
        }
}
