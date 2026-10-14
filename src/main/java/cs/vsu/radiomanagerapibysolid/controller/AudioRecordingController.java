package cs.vsu.radiomanagerapibysolid.controller;

import cs.vsu.radiomanagerapibysolid.dto.AudioRecordingDto;
import cs.vsu.radiomanagerapibysolid.model.enumerate.ApprovalStatus;
import cs.vsu.radiomanagerapibysolid.service.inter.AccessService;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioRecordingService;
import cs.vsu.radiomanagerapibysolid.service.inter.AudioService;
import cs.vsu.radiomanagerapibysolid.service.inter.PricingService;
import cs.vsu.radiomanagerapibysolid.util.AudioUtils;
import cs.vsu.radiomanagerapibysolid.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/audio-recording")
@Tag(name = "Audio recordings", description = "Upload, moderation and download of advertising audio")
@AllArgsConstructor
public class AudioRecordingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudioRecordingController.class);

    private final AudioRecordingService audioRecordingService;

    private final AudioService audioService;

    private final AccessService accessService;

    private final PricingService pricingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all audio recordings",
            description = "Returns a list of all audio recordings."
    )
    public ResponseEntity<List<AudioRecordingDto>> getAllAudioRecordings() {
        LOGGER.info("Fetching all audio recordings");
        List<AudioRecordingDto> audioRecordings = audioRecordingService.getAllRecordings();
        return ResponseEntity.ok(audioRecordings);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get audio recording by ID",
            description = "Returns the audio recording corresponding to the given ID."
    )
    public ResponseEntity<AudioRecordingDto> getAudioRecordingById(@PathVariable Long id) {
        LOGGER.info("Fetching audio recording with ID: {}", id);
        AudioRecordingDto recording = audioRecordingService.getRecordingById(id);
        if (recording != null) {
            return ResponseEntity.ok(recording);
        }

        LOGGER.warn("Audio recording with ID {} not found", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("@accessService.isOwnerOrAdmin(#userId)")
    @Operation(
            summary = "Get audio recordings by user ID",
            description = "Returns all audio recordings for a specific user."
    )
    public ResponseEntity<List<AudioRecordingDto>> getRecordingsByUserId(@PathVariable Long userId) {
        LOGGER.info("Fetching audio recordings for user: {}", userId);
        List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByUserId(userId);
        return ResponseEntity.ok(recordings);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get audio recordings by approval status",
            description = "Returns all audio recordings filtered by the given approval status (APPROVED, PENDING, REJECTED)."
    )
    public ResponseEntity<List<AudioRecordingDto>> getRecordingsByStatus(
            @PathVariable @Parameter(schema = @Schema(implementation = ApprovalStatus.class)) String status) {
        LOGGER.info("Fetching audio recordings with status: {}", status);
        ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
        List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByStatus(approvalStatus);
        return ResponseEntity.ok(recordings);
    }

    @GetMapping("/status_user")
    @PreAuthorize("@accessService.isOwnerOrAdmin(#userId)")
    @Operation(
            summary = "Get audio recordings by approval status and user ID",
            description = "Returns audio recordings filtered by approval status and user ID extracted from request headers. Headers: 'approvalStatus' and 'userId'."
    )
    public ResponseEntity<List<AudioRecordingDto>> getRecordingsByStatusAndUserId(
            @RequestHeader("approvalStatus") @Parameter(schema = @Schema(implementation = ApprovalStatus.class)) String status,
            @RequestHeader("userId") Long userId) {
        LOGGER.info("Fetching audio recordings with status {} for user {}", status, userId);
        ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
        List<AudioRecordingDto> recordings = audioRecordingService.getRecordingByStatusAndUserId(approvalStatus, userId);
        return ResponseEntity.ok(recordings);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN')")
    @Operation(
            summary = "Create new audio recording",
            description = "Uploads an audio file, calculates its duration and cost, creates a new" +
                    " audio recording in the database, and saves the physical file with a unique filename" +
                    " based on the generated audio recording ID."
    )
    public ResponseEntity<AudioRecordingDto> createRecording(@RequestParam("file") MultipartFile file) {
        Long userId = accessService.getCurrentUserId();
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        LOGGER.info("User {} is uploading file: {}", userId, originalFilename);

        double duration = audioService.getAudioDuration(file);

        AudioRecordingDto recordingDto = new AudioRecordingDto();
        recordingDto.setUserId(userId);
        recordingDto.setFilePath(originalFilename);
        recordingDto.setDuration(duration);
        recordingDto.setCost(pricingService.calculateRecordingCost(duration));
        recordingDto.setApprovalStatus(ApprovalStatus.PENDING);
        AudioRecordingDto created = audioRecordingService.createRecording(recordingDto);

        audioService.saveFile(file, created.getId());
        LOGGER.info("Created audio recording with ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update audio recording",
            description = "Updates an existing audio recording."
    )
    public ResponseEntity<AudioRecordingDto> updateRecording(@RequestBody @Valid AudioRecordingDto recordingDto) {
        LOGGER.info("Updating audio recording: {}", recordingDto);
        AudioRecordingDto updated = audioRecordingService.updateRecording(recordingDto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }

        LOGGER.warn("Audio recording with ID {} not found for update", recordingDto.getId());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADVERTISER', 'ADMIN') and @accessService.isRecordingOwnerOrAdmin(#id)")
    @Operation(
            summary = "Delete audio recording",
            description = "Deletes an audio recording and its file by ID."
    )
    public ResponseEntity<Void> deleteRecording(@PathVariable Long id) {
        LOGGER.info("Deleting audio recording with ID: {}", id);
        AudioRecordingDto deleted = audioRecordingService.deleteRecording(id);
        if (deleted == null) {
            LOGGER.warn("Audio recording with ID {} not found for delete", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        audioService.deleteFile(deleted.getId(), deleted.getFilePath());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update audio recording status",
            description = "Updates the approval status of an audio recording by its ID. Valid statuses: APPROVED, PENDING, REJECTED."
    )
    public ResponseEntity<AudioRecordingDto> updateRecordingStatus(
            @PathVariable Long id,
            @RequestParam @Parameter(schema = @Schema(implementation = ApprovalStatus.class)) String status) {
        LOGGER.info("Updating status of audio recording {} to {}", id, status);
        ApprovalStatus approvalStatus = ApprovalStatus.valueOf(status.toUpperCase());
        AudioRecordingDto updated = audioRecordingService.updateRecordingStatus(id, approvalStatus);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }

        LOGGER.warn("Audio recording with ID {} not found for status update", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Download audio file",
            description = "Downloads the actual MP3 file for the given audio recording ID."
    )
    public ResponseEntity<Resource> downloadRecording(@PathVariable Long id) {
        LOGGER.info("Downloading audio file for recording ID: {}", id);
        AudioRecordingDto recording = audioRecordingService.getRecordingById(id);
        if (recording == null) {
            LOGGER.warn("Audio recording with ID {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        byte[] fileData = audioService.getFile(recording.getId(), recording.getFilePath());
        return FileUtils.getFileResponse(fileData, recording.getFilePath(), AudioUtils.AUDIO_MPEG);
    }

    @GetMapping("/approval_statuses")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get available approval statuses for audio recording",
            description = "Retrieves a list of all available approval statuses for audio recordings." +
                    " This is useful for populating status selection dropdowns in the UI."
    )
    public ResponseEntity<List<ApprovalStatus>> getApprovalStatuses() {
        LOGGER.info("Fetching available approval statuses for audio");
        List<ApprovalStatus> statuses = Arrays.asList(ApprovalStatus.values());
        return ResponseEntity.ok(statuses);
    }

}
