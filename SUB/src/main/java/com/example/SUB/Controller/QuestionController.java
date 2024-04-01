package com.example.SUB.Controller;

import com.example.SUB.DTO.AnswerForm;
import com.example.SUB.Entity.Answer;
import com.example.SUB.Entity.Question;
import com.example.SUB.DTO.QuestionForm;
import com.example.SUB.Entity.SiteUser;
import com.example.SUB.Repository.QuestionRepository;
import com.example.SUB.Service.AnswerService;
import com.example.SUB.Service.QuestionService;
import com.example.SUB.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;


    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "") String sort) {
        Page<Question> paging = this.questionService.getList(page, kw, sort);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort); // 추가: sort 파라미터 전달

        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Integer id, AnswerForm answerForm,
                         @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String sort) {
        Question question = questionService.getQuestion(id);
        Page<Answer> paging = answerService.getAnswerPage(question, page, sort);
        model.addAttribute("question", question);
        model.addAttribute("paging", paging);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()") // 로그인 했을때만 실행되는 애너테이션 메서드
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()") // 로그인 했을때만 실행되는 애너테이션 메서드
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal){
        if(bindingResult.hasErrors()){
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(),questionForm.getContent(),siteUser);
        return "redirect:/question/list";
    }

    //객체에 id값으로 조회한 질문의 제목(subject)과 내용(object)의 값을 담아서 템플릿으로 전달
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm,@PathVariable("id") Integer id,Principal principal){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        //질문 의 기존값 유지
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    // 수정 버튼 누르면 데이터 입력 되면서 수정.
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    //삭제 버튼 클릭시에 삭제
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal,@PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    // 추천 기능 siteurser 을 voter로 저장
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    // 조회수 순으로 정렬된 목록을 요청하는 핸들러


}
