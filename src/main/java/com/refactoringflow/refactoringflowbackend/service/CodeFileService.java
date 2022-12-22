package com.refactoringflow.refactoringflowbackend.service;

import com.refactoringflow.refactoringflowbackend.model.AssignmentCodeFileStudent;
import com.refactoringflow.refactoringflowbackend.model.assignment.Assignment;
import com.refactoringflow.refactoringflowbackend.model.codefile.CodeFile;
import com.refactoringflow.refactoringflowbackend.model.codefile.Step;
import com.refactoringflow.refactoringflowbackend.model.codefile.StepDTO;
import com.refactoringflow.refactoringflowbackend.model.user.Student;
import com.refactoringflow.refactoringflowbackend.repository.AssigmentCodeFileStudentRepository;
import com.refactoringflow.refactoringflowbackend.repository.CodeFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CodeFileService {

    private final CodeFileRepository codeFileRepository;
    private final AssignmentService assignmentService;
    private final AssigmentCodeFileStudentRepository assigmentCodeFileStudentRepository;
    private final StudentService studentService;
    private final AlgorithmService algorithmService;

    @Autowired
    public CodeFileService(CodeFileRepository codeFileRepository,
                           AssignmentService assignmentService,
                           AssigmentCodeFileStudentRepository assigmentCodeFileStudentRepository,
                           StudentService studentService,
                           AlgorithmServiceImpl algorithmService) {
        this.codeFileRepository = codeFileRepository;
        this.assignmentService = assignmentService;
        this.assigmentCodeFileStudentRepository = assigmentCodeFileStudentRepository;
        this.studentService = studentService;
        this.algorithmService = algorithmService;
    }

    public void save(CodeFile codeFile, Long assignmentId, Long userId) {
        Optional<Assignment> assignment = assignmentService.findById(assignmentId);
        Optional<Student> student = studentService.findById(userId);
        codeFileRepository.save(codeFile);
        assigmentCodeFileStudentRepository.save(
                new AssignmentCodeFileStudent(student.get(),
                        assignment.orElseThrow(),
                        codeFile)
        );
    }

    public CodeFile getFile(long id){
        return codeFileRepository.findById(id).orElseThrow();
    }

    public CodeFile getTemplate(long assignmentId){
        Assignment assignment = assignmentService.findById(assignmentId).orElseThrow();
        List<AssignmentCodeFileStudent> assignmentCodeFileStudent = assigmentCodeFileStudentRepository.findAssignmentCodeFileStudentByAssignment(assignment);
        for (AssignmentCodeFileStudent a : assignmentCodeFileStudent){
            if(a.getCodeFile().getVersion()==0){
                return a.getCodeFile();
            }
        }
        return null;
    }

    public CodeFile findCodefileByAssignment(long assignmentId){
        List<AssignmentCodeFileStudent> assignmentCodeFileStudents;
        assignmentCodeFileStudents = assigmentCodeFileStudentRepository.findAssignmentCodeFileStudentByAssignment(assignmentService.findById(assignmentId).get());
       for (AssignmentCodeFileStudent codeFile: assignmentCodeFileStudents){
           if (codeFile.getCodeFile().getVersion() == 0){
               return codeFile.getCodeFile();
           }
       }
       return null;
    }

    public List<StepDTO> getSteps(Long id) {
        CodeFile codeFile = codeFileRepository.findById(id).orElseThrow();
        List<Step> steps = algorithmService.generateSteps(codeFile);
        return steps.stream().map(
                (step) -> new StepDTO(step.getId(), step.getStepIndex(), id,
                        step.getTitle(), step.getDescription())).toList();
    }
}
