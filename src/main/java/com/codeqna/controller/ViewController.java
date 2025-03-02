package com.codeqna.controller;


import com.codeqna.dto.BoardViewDto;
import com.codeqna.dto.LogsViewDto;
import com.codeqna.dto.RepliesViewDto;
import com.codeqna.dto.UserFormDto;
import com.codeqna.dto.request.ArticleCommentRequest;
import com.codeqna.dto.response.ArticleCommentResponse;
import com.codeqna.dto.response.ArticleResponse;
import com.codeqna.dto.security.BoardPrincipal;
import com.codeqna.entity.*;
import com.codeqna.repository.BoardRepository;
import com.codeqna.repository.FileconfigRepository;
import com.codeqna.repository.UploadfileRepository;
import com.codeqna.repository.UserRepository;
import com.codeqna.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserRepository userRepository;
    private final BoardService boardService;
    private final BoardRepository repository;
    private final ReplyService replyService;
    private final ArticleCommentService articleCommentService;
    private final UserService userService;
    private final UploadfileRepository uploadfileRepository;
    private final VisitorService visitorService;
    private final FileconfigRepository fileconfigRepository;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("/main")
    public String boardList(Model model,@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);
        // 게시글 목록을 가져와서 모델에 추가
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);


        if(boardPrincipal != null) {
            String email = boardPrincipal.getUsername();
            Users users = userRepository.findByEmail(email).orElseThrow();
            model.addAttribute("nickname", users.getNickname());
            model.addAttribute("loggedIn", "true");

        }else{
            model.addAttribute("loggedIn", "false");
        }

        return "boardlist"; // HTML 템플릿 이름 리턴
    }

    @GetMapping("/admin/deleted")
    public String deletedBoard(Model model){
        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);

        List<LogsViewDto> boards = boardService.getLogWithBoard();
        model.addAttribute("boards", boards);
        return "admin/deletedBoards";
    }

    @GetMapping("/admin/boards")
    public String manageBoards(Model model){
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);
        return "admin/manageBoards";
    }

    @GetMapping("/admin/comments")
    public String manageComments(Model model){

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);
        List<RepliesViewDto> replies = articleCommentService.getReplies();
        model.addAttribute("replies", replies);
        return "admin/manageComments";}

    @GetMapping("/admin/users")
    public String manageUsers(Model model){
        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);
        List<Users> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/manageUsers";
    }
    @GetMapping("/admin/files")
    public String manageFiles(Model model){

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);

        Fileconfig fileconfig = fileconfigRepository.findById(1).orElse(new Fileconfig());

        model.addAttribute("maxFileNum", fileconfig.getMax_File_Num());
        model.addAttribute("maxFileSize",fileconfig.getMax_file_Size());
        model.addAttribute("fileExt",fileconfig.getFile_ext());
        return "admin/manageFiles";
    }

    @GetMapping("/admin/visitorDashboard")
    public String visitorDashboard(Model model){

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);
        return "admin/visitorDashboard";
    }

    @GetMapping("/newboard")
    public String newboard(Model model, @AuthenticationPrincipal BoardPrincipal boardPrincipal ){
        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);

        String email = boardPrincipal.getUsername();
        Users users = userRepository.findByEmail(email).orElseThrow();

        model.addAttribute("nickname", users.getNickname());

        model.addAttribute("board", new BoardViewDto());

        return "newboard";
    }

    @GetMapping("/modifyboard")
    public String modifyboard(@RequestParam(required = false) Long bno, Model model
    ,  @AuthenticationPrincipal BoardPrincipal boardPrincipal){

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);

        String email = boardPrincipal.getUsername();
        Users users = userRepository.findByEmail(email).orElseThrow();
        model.addAttribute("nickname", users.getNickname());

        //지금은 board 데이터 전부 다 넘겨주고 있는데
        //나중에 제목, 내용, 해시태그, 첨부파일 이것만 보내주면 댐
        Board board = boardService.findByBno(bno);
        model.addAttribute("board", board);
        return "modifyboard";
    }

    @GetMapping("/viewboard/{bno}")
    public String viewBoard(@PathVariable Long bno, Model model
            , @AuthenticationPrincipal BoardPrincipal boardPrincipal
                          ) {

        //방문자수
        long visitorCnt = visitorService.getTodayVisitor();
        model.addAttribute("visitorCnt", visitorCnt);

        Board board = boardService.findByBno(bno);
        //없는 게시물에 들어갔을 때
        if(board == null){
            return "error2";
        }

        ArticleResponse articleResponse = ArticleResponse.from(board);

        Set<ArticleCommentResponse> reply = articleCommentService.searchArticleComments(bno);


        //삭제된 게시물에 접근할 때
        if(board.getBoard_condition().equals("Y")) {
            return "error2";
        }

        //정상적인 페이지로 들어갔을 경우 조회수 + 1
        repository.incrementHitCount(bno);

        String hashtags = board.getHashtag();
        List<String> hashtagList = Arrays.asList(hashtags.split("#"));
        // 빈 문자열을 제외합니다.
        hashtagList = hashtagList.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
        model.addAttribute("hashtags", hashtagList);

        //로그인된 사용자의 nickname을 넘김, 없으면 오류, 빈 값을 넘김
        if(boardPrincipal!=null) {
            String email = boardPrincipal.getUsername();
            Users users = userRepository.findByEmail(email).orElseThrow();
            model.addAttribute("nickname", users.getNickname());
            model.addAttribute("loggedIn", "true");
        } else{
            model.addAttribute("nickname", "");
            model.addAttribute("loggedIn", "false");
        }
        // 파일정보 가져오는 부분
        List<Uploadfile> uploadfiles = uploadfileRepository.findByBoard_Bno(bno);

        model.addAttribute("uploadPath", uploadPath);

        model.addAttribute("files", uploadfiles);

        model.addAttribute("board", articleResponse);


        model.addAttribute("reply", reply);

        long totalCount = boardService.getTotalCount();
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("boardBno", bno);

        Optional<Board> previousBoard = boardService.getPreviousActiveBoard(bno);
        Optional<Board> nextBoard = boardService.getNextActiveBoard(bno);

        model.addAttribute("previousBoard", previousBoard.orElse(null));
        model.addAttribute("nextBoard", nextBoard.orElse(null));
        return "viewBoard";
    }
    @GetMapping("/comments")
    @ResponseBody
    public Set<ArticleCommentResponse> getComments(@RequestParam("articleId") Long articleId) {
        return articleCommentService.searchArticleComments(articleId);
    }




}
