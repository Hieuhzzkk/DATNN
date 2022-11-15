package com.example.demo.Controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entities.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.SendMailService;


@Controller
public class AccountController {

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	HttpSession session;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SendMailService sendMailService;

	@GetMapping(value = "/forgotPassword")
	public String forgotPassword() {

		return "web/forgotPassword";
	}

	@PostMapping("/forgotPassword")
	public ModelAndView forgotPassowrd(ModelMap model, @RequestParam("email") String email) {
		List<User> listUser = userRepository.findAll();
		for (User user : listUser) {
			if (email.trim().equals(user.getEmail())) {
				session.removeAttribute("otp");
				int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
				session.setAttribute("otp", random_otp);
				String body = "<div>\r\n" + "<h3>Mã xác thực OTP của bạn là: <span style=\"color:#119744; font-weight: bold;\">"
						+ random_otp + "</span></h3>\r\n" + "</div>";
				sendMailService.queue(email, "Quên mật khẩu?", body);

				model.addAttribute("email", email);
				model.addAttribute("message", "Mã xác thực OTP đã được gửi tới Email : " + user.getEmail() + " , hãy kiểm tra Email của bạn!");
				return new ModelAndView("/web/confirmOtpForgotPassword", model);
			}
		}
		model.addAttribute("error", "Email này chưa đăng ký!");
		return new ModelAndView("web/forgotPassword", model);
	}
	

}
