package net.ravik_cms.ravik_backend.common.dataInitializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ravik_cms.ravik_backend.account.dtos.CreateAccountDto;
import net.ravik_cms.ravik_backend.account.service.AccountService;
import net.ravik_cms.ravik_backend.budget.dtos.CreateBudgetDto;
import net.ravik_cms.ravik_backend.budget.service.BudgetService;
import net.ravik_cms.ravik_backend.calendar.CalendarService;
import net.ravik_cms.ravik_backend.calendar.CreateCalendarDto;
import net.ravik_cms.ravik_backend.common.enums.BudgetCategory;
import net.ravik_cms.ravik_backend.common.enums.EquipmentCategory;
import net.ravik_cms.ravik_backend.common.enums.PaymentFrequency;
import net.ravik_cms.ravik_backend.equipment.dtos.CreateEquipmentDto;
import net.ravik_cms.ravik_backend.equipment.entity.Equipments;
import net.ravik_cms.ravik_backend.equipment.repository.EquipmentRepository;
import net.ravik_cms.ravik_backend.equipment.service.EquipmentService;
import net.ravik_cms.ravik_backend.expenseCategory.dto.CreateExpenseCategoryDto;
import net.ravik_cms.ravik_backend.expenseCategory.service.ExpenseCategoryService;
import net.ravik_cms.ravik_backend.jobTitles.CreateJobTitleDto;
import net.ravik_cms.ravik_backend.jobTitles.JobTitles;
import net.ravik_cms.ravik_backend.jobTitles.JobTitlesRepository;
import net.ravik_cms.ravik_backend.jobTitles.JobTitlesService;
import net.ravik_cms.ravik_backend.milestoneBudget.dtos.CreateMilestoneBudgetLineDto;
import net.ravik_cms.ravik_backend.milestoneBudget.service.MilestoneBudgetService;
import net.ravik_cms.ravik_backend.milestoneScheduling.service.DependencyService;
import net.ravik_cms.ravik_backend.milestones.dtos.CreateMilestoneDto;
import net.ravik_cms.ravik_backend.milestones.dtos.MilestoneInfoDto;
import net.ravik_cms.ravik_backend.milestones.service.MilestonesService;
import net.ravik_cms.ravik_backend.phase.dtos.CreatePhaseDto;
import net.ravik_cms.ravik_backend.phase.dtos.PhasesInfoDto;
import net.ravik_cms.ravik_backend.phase.service.PhasesService;
import net.ravik_cms.ravik_backend.projects.ProjectDto;
import net.ravik_cms.ravik_backend.projects.ProjectInfoDto;
import net.ravik_cms.ravik_backend.projects.ProjectOrchestrationService;
import net.ravik_cms.ravik_backend.projects.ProjectsRepository;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.dtos.CreateEquipmentRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.equipmentRequired.service.EquipmentRequiredService;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.dtos.CreateLaborRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.laborRequired.service.LaborRequiredService;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.dtos.CreateSubContractorRequiredDto;
import net.ravik_cms.ravik_backend.resourcesRequired.subContractorRequired.service.SubContractorRequiredService;
import net.ravik_cms.ravik_backend.subContractor.dtos.CreateSubContractorDto;
import net.ravik_cms.ravik_backend.subContractor.entity.SubContractor;
import net.ravik_cms.ravik_backend.subContractor.repository.SubContractorRepository;
import net.ravik_cms.ravik_backend.subContractor.service.SubContractorService;
import net.ravik_cms.ravik_backend.users.ClientDto;
import net.ravik_cms.ravik_backend.users.CreateClientDto;
import net.ravik_cms.ravik_backend.users.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Rebuilds a minimal usable dataset (one login, one project, and enough
 * reference/config data to exercise scheduling, budgeting and payouts)
 * whenever the local dev database is empty. Runs after {@link PermissionsSeeder}.
 */
@Slf4j
@Order(3)
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.dev-data", havingValue = "true", matchIfMissing = true)
public class DevDataSeeder implements CommandLineRunner {

    private static final String SEED_USERNAME = "admin";
    private static final String SEED_PASSWORD = "Password123!";

    private final ProjectsRepository projectsRepository;
    private final UserService userService;
    private final ProjectOrchestrationService projectOrchestrationService;
    private final JobTitlesService jobTitlesService;
    private final JobTitlesRepository jobTitlesRepository;
    private final CalendarService calendarService;
    private final PhasesService phasesService;
    private final MilestonesService milestonesService;
    private final DependencyService dependencyService;
    private final EquipmentService equipmentService;
    private final EquipmentRepository equipmentRepository;
    private final SubContractorService subContractorService;
    private final SubContractorRepository subContractorRepository;
    private final ExpenseCategoryService expenseCategoryService;
    private final AccountService accountService;
    private final BudgetService budgetService;
    private final MilestoneBudgetService milestoneBudgetService;
    private final LaborRequiredService laborRequiredService;
    private final EquipmentRequiredService equipmentRequiredService;
    private final SubContractorRequiredService subContractorRequiredService;

    @Override
    public void run(String... args) {
        if (projectsRepository.count() > 0) {
            return;
        }

        ClientDto client = seedClient();
        ProjectInfoDto project = seedProject(client);
        UUID projectId = project.getId();

        List<Long> jobTitleIds = seedJobTitles(projectId);
        seedCalendar(projectId);
        List<Long> equipmentIds = seedEquipment(projectId);
        List<Long> subContractorIds = seedSubContractors(projectId);
        seedExpenseCategories(projectId);
        seedAccount(client.getId());
        seedBudget(projectId);

        PhasesInfoDto phase = seedPhase(projectId);
        List<MilestoneInfoDto> milestones = seedMilestones(projectId);
        seedDependencies(milestones);

        UUID firstMilestoneId = UUID.fromString(milestones.get(0).getId());
        seedMilestoneBudget(firstMilestoneId);
        seedLaborRequired(firstMilestoneId, jobTitleIds);
        seedEquipmentRequired(firstMilestoneId, equipmentIds);
        seedSubContractorRequired(firstMilestoneId, subContractorIds);

        log.info("Dev data seeded. Log in with username '{}' / password '{}'. Project '{}' ({})",
                SEED_USERNAME, SEED_PASSWORD, project.getTitle(), projectId);
    }

    private ClientDto seedClient() {
        CreateClientDto dto = new CreateClientDto(
                SEED_USERNAME, "admin@ravik.local", SEED_PASSWORD, "+254712345678", "ID00000001", null
        );
        return userService.addClient(dto);
    }

    private ProjectInfoDto seedProject(ClientDto client) {
        ProjectDto dto = new ProjectDto(
                "Riverside Apartments", "Nairobi, Kenya", "LR-2026-001", "Along Riverside Drive",
                "Residential", 2500.0, null, LocalDate.now()
        );
        return projectOrchestrationService.createProject(dto, client.getUserName());
    }

    private List<Long> seedJobTitles(UUID projectId) {
        jobTitlesService.addJobTitle(new CreateJobTitleDto("Site Supervisor", 3000.0, PaymentFrequency.MONTHLY), projectId);
        jobTitlesService.addJobTitle(new CreateJobTitleDto("Mason", 1500.0, PaymentFrequency.DAILY), projectId);
        jobTitlesService.addJobTitle(new CreateJobTitleDto("Laborer", 800.0, PaymentFrequency.DAILY), projectId);

        return jobTitlesRepository.findAll().stream()
                .filter(jt -> jt.getProject().getId().equals(projectId))
                .map(JobTitles::getId)
                .toList();
    }

    private void seedCalendar(UUID projectId) {
        CreateCalendarDto dto = new CreateCalendarDto(
                "Standard work week", true, true, true, true, true, true, false, new HashSet<>()
        );
        calendarService.createCalender(projectId, dto);
    }

    private List<Long> seedEquipment(UUID projectId) {
        equipmentService.addEquipment(new CreateEquipmentDto("Concrete Mixer", EquipmentCategory.OWNED, 500L, "litres"), projectId);
        equipmentService.addEquipment(new CreateEquipmentDto("Excavator", EquipmentCategory.RENTED, 1L, "unit"), projectId);

        return equipmentRepository.findAll().stream()
                .filter(e -> e.getProject().getId().equals(projectId))
                .map(Equipments::getId)
                .toList();
    }

    private List<Long> seedSubContractors(UUID projectId) {
        subContractorService.addSubContractor(
                new CreateSubContractorDto("Nairobi Electricals Ltd", "Electrical", "info@nbielectricals.co.ke",
                        "Industrial Area, Nairobi", "+254722000111"),
                projectId
        );

        return subContractorRepository.findAll().stream()
                .filter(sc -> sc.getProject().getId().equals(projectId))
                .map(SubContractor::getId)
                .toList();
    }

    private void seedExpenseCategories(UUID projectId) {
        expenseCategoryService.addExpenseCategory(
                new CreateExpenseCategoryDto("Site Materials", BudgetCategory.MATERIAL), projectId);
        expenseCategoryService.addExpenseCategory(
                new CreateExpenseCategoryDto("General Site Costs", BudgetCategory.OTHERS), projectId);
    }

    private void seedAccount(UUID clientId) {
        accountService.addAccount(new CreateAccountDto("Main Project Account", "BANK", 0.0), clientId);
    }

    private void seedBudget(UUID projectId) {
        budgetService.addBudget(new CreateBudgetDto(4_000_000.0, BudgetCategory.MATERIAL), projectId);
        budgetService.addBudget(new CreateBudgetDto(1_500_000.0, BudgetCategory.LABOUR), projectId);
    }

    private PhasesInfoDto seedPhase(UUID projectId) {
        return phasesService.createPhase(projectId, new CreatePhaseDto("Substructure", "Foundation and groundworks"));
    }

    private List<MilestoneInfoDto> seedMilestones(UUID projectId) {
        MilestoneInfoDto foundation = milestonesService.createMilestone(
                projectId, new CreateMilestoneDto("Foundation Excavation", "Excavate and cast foundation", 10));
        MilestoneInfoDto framing = milestonesService.createMilestone(
                projectId, new CreateMilestoneDto("Structural Framing", "Cast columns and beams", 20));
        MilestoneInfoDto roofing = milestonesService.createMilestone(
                projectId, new CreateMilestoneDto("Roofing", "Install roof structure and covering", 15));
        return List.of(foundation, framing, roofing);
    }

    private void seedDependencies(List<MilestoneInfoDto> milestones) {
        if (milestones.size() < 3) {
            return;
        }
        UUID foundationId = UUID.fromString(milestones.get(0).getId());
        UUID framingId = UUID.fromString(milestones.get(1).getId());
        UUID roofingId = UUID.fromString(milestones.get(2).getId());

        dependencyService.addPredecessor(framingId, foundationId);
        dependencyService.addPredecessor(roofingId, framingId);
    }

    private void seedMilestoneBudget(UUID milestoneId) {
        milestoneBudgetService.createBudgetLines(milestoneId, List.of(
                new CreateMilestoneBudgetLineDto(BudgetCategory.LABOUR, 300_000.0),
                new CreateMilestoneBudgetLineDto(BudgetCategory.MATERIAL, 800_000.0),
                new CreateMilestoneBudgetLineDto(BudgetCategory.PLANT_AND_EQUIPMENT, 150_000.0)
        ));
    }

    private void seedLaborRequired(UUID milestoneId, List<Long> jobTitleIds) {
        if (jobTitleIds.isEmpty()) {
            return;
        }
        laborRequiredService.createLaborRequired(milestoneId, List.of(
                new CreateLaborRequiredDto(jobTitleIds.get(0), 1),
                new CreateLaborRequiredDto(jobTitleIds.get(jobTitleIds.size() - 1), 5)
        ));
    }

    private void seedEquipmentRequired(UUID milestoneId, List<Long> equipmentIds) {
        if (equipmentIds.isEmpty()) {
            return;
        }
        equipmentRequiredService.createEquipmentRequired(milestoneId, List.of(
                new CreateEquipmentRequiredDto(equipmentIds.get(0), 5, 200, "Foundation concrete works", LocalDate.now())
        ));
    }

    private void seedSubContractorRequired(UUID milestoneId, List<Long> subContractorIds) {
        if (subContractorIds.isEmpty()) {
            return;
        }
        subContractorRequiredService.createSubContractorRequired(milestoneId, List.of(
                new CreateSubContractorRequiredDto(subContractorIds.get(0), LocalDate.now(),
                        120_000.0, "Site Electrical Wiring", "Temporary site power and wiring")
        ));
    }
}
