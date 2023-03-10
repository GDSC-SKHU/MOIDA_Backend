package gdsc.skhu.moida.controller;

import gdsc.skhu.moida.domain.DTO.PostDTO;
import gdsc.skhu.moida.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @PostMapping("/new")
    public ResponseEntity<String> write(Principal principal, @RequestBody PostDTO postDTO) {
        postService.write(principal, postDTO);
        return ResponseEntity.ok("Create new post success");
    }

    @GetMapping("/list/{pageNumber}")
    public Slice<PostDTO> list(@PathVariable("pageNumber") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findAllWithPaging(pageable);
    }

    @GetMapping("/type/{type}/{pageNumber}")
    public Slice<PostDTO> typeList(@PathVariable("type") String type,
                                   @PathVariable("pageNumber") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber == 0 ? 0 : pageNumber-1, 9, Sort.by("id").descending());
        return postService.findByTypeWithPaging(pageable, type);
    }

    @GetMapping("/{id}")
    public PostDTO show(@PathVariable Long id) {
        return postService.findById(id);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<PostDTO> edit(Principal principal, @PathVariable Long id) {
        PostDTO postDTO = postService.findById(id);
        if(principal.getName().equals(postDTO.getAuthor())) {
            return ResponseEntity.ok(postDTO);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<String> edit(Principal principal, @PathVariable Long id, @RequestBody PostDTO postDTO) {
        PostDTO oldPostDTO = postService.findById(id);
        if(principal.getName().equals(postDTO.getAuthor())) {
            postDTO.setId(oldPostDTO.getId());
            postService.edit(postDTO);
            return ResponseEntity.ok("Update post success");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(Principal principal, @PathVariable Long id) {
        PostDTO postDTO = postService.findById(id);
        if(principal.getName().equals(postDTO.getAuthor())) {
            postService.delete(id);
            return ResponseEntity.ok("Delete post success");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("UNAUTHORIZED");
    }
}