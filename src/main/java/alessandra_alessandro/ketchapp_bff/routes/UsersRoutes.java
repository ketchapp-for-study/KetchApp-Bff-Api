package alessandra_alessandro.ketchapp_bff.routes;

import alessandra_alessandro.ketchapp_bff.annotations.CurrentUser;
import alessandra_alessandro.ketchapp_bff.controllers.UsersControllers;
import alessandra_alessandro.ketchapp_bff.models.responses.*;
import alessandra_alessandro.ketchapp_bff.models.responses.AchievementResponse;
import alessandra_alessandro.ketchapp_bff.models.responses.ActivityResponse;
import alessandra_alessandro.ketchapp_bff.models.responses.StatisticsResponse;
import alessandra_alessandro.ketchapp_bff.models.responses.TomatoResponse;
import alessandra_alessandro.ketchapp_bff.models.responses.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/users")
@RestController
public class UsersRoutes {

    @Autowired
    UsersControllers usersController;

    @GetMapping("@me")
    public ResponseEntity<UserResponse> getCurrentUser(
        @CurrentUser UserResponse user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        // Recupera i dati completi dell'utente usando lo username (o uuid se disponibile)
        UserResponse fullUser = usersController.getUser(user.getId());
        if (fullUser == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(fullUser);
    }

    @GetMapping("@me/achievements")
    public ResponseEntity<List<AchievementResponse>> getCurrentUserAchievements(
        @CurrentUser UserResponse user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<AchievementResponse> achievements =
            usersController.getUserAchievements(user.getId());
        if (achievements == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(achievements);
    }

    @GetMapping("@me/statistics")
    public ResponseEntity<StatisticsResponse> getCurrentUserStatistics(
        @CurrentUser UserResponse user,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        StatisticsResponse statistics = usersController.getUserStatistics(
            user.getId(),
            startDate,
            endDate
        );
        if (statistics == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("@me/tomatoes")
    public ResponseEntity<List<TomatoResponse>> getCurrentUserTomatoes(
        @CurrentUser UserResponse user,
        @RequestParam(value = "date", required = false) LocalDate date,
        @RequestParam(
            value = "startDate",
            required = false
        ) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<TomatoResponse> tomatoes;
        UUID uuid = user.getId();
        if (startDate != null && endDate != null) {
            tomatoes = usersController.getUserTomatoes(
                uuid,
                startDate,
                endDate
            );
        } else if (date != null) {
            tomatoes = usersController.getUserTomatoes(uuid, date);
        } else {
            tomatoes = usersController.getUserTomatoes(uuid);
        }
        return ResponseEntity.ok(tomatoes);
    }

    @GetMapping("@me/activities")
    public ResponseEntity<List<ActivityResponse>> getCurrentUserActivities(
        @CurrentUser UserResponse user
    ) {
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<ActivityResponse> activities = usersController.getUserActivities(
            user.getId()
        );
        if (activities == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(activities);
    }

    @Operation(
        summary = "Get all users",
        description = "Fetches a list of all user records."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user records",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        List<UserResponse> users = usersController.getUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Get user by UUID",
        description = "Fetches a user record by its UUID."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user record",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            ),
        }
    )
    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID uuid) {
        UserResponse user = usersController.getUser(uuid);
        if (user == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Get email by username",
        description = "Fetches the email address associated with a given username."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved email address",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Username not found"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/email/{username}")
    public ResponseEntity<String> getEmailByUsername(
        @PathVariable String username
    ) {
        String email = usersController.getEmailByUsername(username);
        if (email == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(email);
    }

    @Operation(
        summary = "Get user's Tomatoes",
        description = "Fetches the number of tomatoes for a user by their UUID."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved user's tomatoes",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TomatoResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/{uuid}/tomatoes")
    public ResponseEntity<List<TomatoResponse>> getUserTomatoes(
        @PathVariable UUID uuid,
        @RequestParam(value = "date", required = false) LocalDate date,
        @RequestParam(
            value = "startDate",
            required = false
        ) LocalDate startDate,
        @RequestParam(value = "endDate", required = false) LocalDate endDate
    ) {
        try {
            List<TomatoResponse> tomatoes;
            if (startDate != null && endDate != null) {
                tomatoes = usersController.getUserTomatoes(
                    uuid,
                    startDate,
                    endDate
                );
            } else if (date != null) {
                tomatoes = usersController.getUserTomatoes(uuid, date);
            } else {
                tomatoes = usersController.getUserTomatoes(uuid);
            }
            return ResponseEntity.ok(tomatoes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Get activities by user UUID",
        description = "Fetches a list of activities for a specific user by their UUID."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved activities for user",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ActivityResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/{uuid}/activities")
    public ResponseEntity<List<ActivityResponse>> getUserActivities(
        @PathVariable UUID uuid
    ) {
        List<ActivityResponse> activities = usersController.getUserActivities(
            uuid
        );
        if (activities == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(activities);
    }

    @Operation(
        summary = "Get achievements by user UUID",
        description = "Fetches a list of achievements for a specific user by their UUID."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved achievements for user",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AchievementResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/{uuid}/achievements")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(
        @PathVariable UUID uuid
    ) {
        List<AchievementResponse> achievements =
            usersController.getUserAchievements(uuid);
        if (achievements == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(achievements);
    }

    @Operation(
        summary = "Get statistics by user UUID",
        description = "Fetches statistics for a specific user by their UUID."
    )
    @ApiResponses(
        value = {
            @ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved statistics for user",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = StatisticsResponse.class)
                )
            ),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            ),
        }
    )
    @GetMapping("/{uuid}/statistics")
    public ResponseEntity<StatisticsResponse> getUserStatistics(
        @PathVariable UUID uuid,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate
    ) {
        StatisticsResponse statistics = usersController.getUserStatistics(
            uuid,
            startDate,
            endDate
        );
        if (statistics == null) {
            return ResponseEntity.status(500).body(null);
        }
        return ResponseEntity.ok(statistics);
    }
}
