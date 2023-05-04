// package com.projects.mypcb.controller;

// import org.springframework.boot.web.servlet.error.ErrorController;
// import org.springframework.http.HttpStatus;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.RequestMapping;

// import jakarta.servlet.RequestDispatcher;
// import jakarta.servlet.http.HttpServletRequest;

// @Controller
// public class CustomErrorController implements ErrorController {

//     @RequestMapping("/error")
//     public String handleError(HttpServletRequest request, Model model) {
//         Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

//         if (status != null) {
//             int statusCode = Integer.parseInt(status.toString());

//             if (statusCode == HttpStatus.FORBIDDEN.value()) {
//                 model.addAttribute("errorMessage", "Access Denied!");
//                 return "error/access-denied";
//             } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
//                 model.addAttribute("errorMessage", "Unauthorized!");
//                 return "error/unauthorized";
//             }
//             // ... Handle other status codes
//         }
//         return "error/default";
//     }

//     public String getErrorPath() {
//         return "/error";
//     }
// }
