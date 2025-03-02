package com.codeqna.controller;

import com.codeqna.dto.FileConfigDTO;
import com.codeqna.dto.UploadFileDto;
import com.codeqna.entity.Fileconfig;
import com.codeqna.entity.Uploadfile;
import com.codeqna.repository.FileconfigRepository;
import com.codeqna.repository.UploadfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/fileAPI")
public class FileConfigController {


    // 파일이 저장될 경로 설정
    @Value("${upload.path}")
    private String uploadPath;


    @Autowired
    private FileconfigRepository fileconfigRepository;
    @Autowired
    private UploadfileRepository uploadfileRepository;

    @PostMapping("/saveFileConfig")
    public ResponseEntity<FileConfigDTO> saveFileConfig(@RequestBody FileConfigDTO fileConfigDTO) {
        // 항상 단일 레코드만 존재하게 처리
        Fileconfig fileconfig = fileconfigRepository.findById(1).orElse(new Fileconfig());
        fileconfig.setId(1); // ID는 항상 1로 고정
        fileconfig.setMax_File_Num(fileConfigDTO.getMaxFileNum());
        fileconfig.setMax_file_Size(fileConfigDTO.getMaxFileSize());
        fileconfig.setFile_ext(fileConfigDTO.getFileExt());

        fileconfigRepository.save(fileconfig);
        return ResponseEntity.ok().
                build();
    }

    @GetMapping("/fileconfig")
    public Fileconfig getFileConfig(){
        Optional<Fileconfig> fileconfig = fileconfigRepository.findById(1);
        return fileconfig.orElseThrow(() -> new RuntimeException("FIle configuration not found"));
    }


    //실제 경로(로컬, no DB)에 저장
    @PostMapping("/upload")
    public ResponseEntity<UploadFileDto> uploadFile(@RequestParam("file") MultipartFile file){

        try {
            // 저장할 파일 경로 생성
            String originalFileName = file.getOriginalFilename();
            String savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path savedFilePath = Paths.get(uploadPath, savedFileName);
            System.out.println("실제 저장된 경로인가:" + savedFilePath);

            // 파일 저장
            Files.createDirectories(savedFilePath.getParent());
            file.transferTo(savedFilePath.toFile());

            // 파일 정보 반환
            UploadFileDto fileDto = new UploadFileDto();
            fileDto.setOriginalFileName(originalFileName);
            fileDto.setSavedFileName(savedFileName);
            return ResponseEntity.status(HttpStatus.OK).body(fileDto);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //게시물에 등록된 파일 가져오기
    //수정 페이지를 들어갔을 때 이미 첨부된 파일 보여주기 위한 메서드
    @GetMapping("/files/{bno}")
    public List<UploadFileDto> getFilesByBoardNo(@PathVariable Long bno) {
        List<Uploadfile> files = uploadfileRepository.findByBoard_Bno(bno);
        return files.stream().map(file -> new UploadFileDto(file.getOriginal_file_name(), file.getSaved_file_name()))
                .collect(Collectors.toList());
    }

    //게시물에 등록된 파일 삭제
    @DeleteMapping("/delete/{bno}")
    public ResponseEntity<String> deleteFile(@RequestBody Map<String, List<String>> request, @PathVariable Long bno) throws IOException {

        List<String> fileNames = request.get("files");

        if (fileNames != null && !fileNames.isEmpty()) {
            for (String fileName : fileNames) {
                // 파일 삭제 로직을 구현합니다.
                // fileName과 bno를 사용하여 파일을 삭제합니다.
                System.out.println("Deleting file: " + fileName + " for board number: " + bno);
                Uploadfile file = uploadfileRepository.findByOriginalFileName(fileName, bno);

                Path filePath = Paths.get(uploadPath, file.getSaved_file_name());
                //System.out.println("파일 경로 : " + filePath);
                Files.deleteIfExists(filePath);
                uploadfileRepository.delete(file);

            }
            return ResponseEntity.ok("Files deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No files to delete.");
        }
    }
}
