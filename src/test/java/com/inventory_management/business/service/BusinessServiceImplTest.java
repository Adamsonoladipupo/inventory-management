package com.inventory_management.business.service;

import com.inventory_management.business.dto.BusinessResponse;
import com.inventory_management.business.dto.CreateBusinessRequest;
import com.inventory_management.business.dto.UpdateBusinessRequest;
import com.inventory_management.business.entity.Business;
import com.inventory_management.business.exception.BusinessNotFoundException;
import com.inventory_management.business.exception.DuplicateBusinessEmailException;
import com.inventory_management.business.repository.BusinessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BusinessServiceImpl")
class BusinessServiceImplTest {

    @Mock
    private BusinessRepository businessRepository;

    @InjectMocks
    private BusinessServiceImpl businessService;

    private static final UUID FIXED_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final Instant FIXED_TIME = Instant.parse("2026-01-01T10:00:00Z");

    private Business buildSavedBusiness(String name, String email, String phone, String address) {
        Business business = new Business();
        business.setId(FIXED_ID);
        business.setName(name);
        business.setEmail(email);
        business.setPhone(phone);
        business.setAddress(address);
        business.setActive(true);
        business.setCreatedAt(FIXED_TIME);
        return business;
    }

    // createBusiness

    @Nested
    @DisplayName("createBusiness()")
    class CreateBusinessTests {

        private CreateBusinessRequest request;

        @BeforeEach
        void setUp() {
            request = new CreateBusinessRequest(
                    "Ade Stores",
                    "ade@adestores.com",
                    "+2348012345678",
                    "12 Lagos Street, Ikeja"
            );
        }

        @Test
        @DisplayName("should return a BusinessResponse with the correct id when business is created successfully")
        void shouldReturnCorrectId() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertThat(response.id()).isEqualTo(FIXED_ID);
        }

        @Test
        @DisplayName("should return a BusinessResponse with the correct name")
        void shouldReturnCorrectName() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertThat(response.name()).isEqualTo("Ade Stores");
        }

        @Test
        @DisplayName("should return a BusinessResponse with the correct email")
        void shouldReturnCorrectEmail() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertThat(response.email()).isEqualTo("ade@adestores.com");
        }

        @Test
        @DisplayName("should return a BusinessResponse with the correct phone")
        void shouldReturnCorrectPhone() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertThat(response.phone()).isEqualTo("+2348012345678");
        }

        @Test
        @DisplayName("should return a BusinessResponse with the correct address")
        void shouldReturnCorrectAddress() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertThat(response.address()).isEqualTo("12 Lagos Street, Ikeja");
        }

        @Test
        @DisplayName("should return a BusinessResponse with active=true when business is created")
        void shouldReturnActiveTrue() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertTrue(response.active());
        }

        @Test
        @DisplayName("should return a BusinessResponse with a non-null createdAt")
        void shouldReturnNonNullCreatedAt() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(request);

            assertNotNull(response.createdAt());
            assertThat(response.createdAt()).isEqualTo(FIXED_TIME);
        }

        @Test
        @DisplayName("should persist a Business entity with correct name from request")
        void shouldPersistCorrectName() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.createBusiness(request);

            verify(businessRepository).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Ade Stores");
        }

        @Test
        @DisplayName("should persist a Business entity with correct email from request")
        void shouldPersistCorrectEmail() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.createBusiness(request);

            verify(businessRepository).save(captor.capture());
            assertThat(captor.getValue().getEmail()).isEqualTo("ade@adestores.com");
        }

        @Test
        @DisplayName("should persist a Business entity with active=true by default")
        void shouldPersistWithActiveTrueByDefault() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.createBusiness(request);

            verify(businessRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should call repository.save exactly once on success")
        void shouldCallSaveExactlyOnce() {
            Business saved = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            businessService.createBusiness(request);

            verify(businessRepository, times(1)).save(any(Business.class));
        }

        @Test
        @DisplayName("should throw DuplicateBusinessEmailException when email is already registered")
        void shouldThrowDuplicateEmailException() {
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(true);

            assertThatThrownBy(() -> businessService.createBusiness(request))
                    .isInstanceOf(DuplicateBusinessEmailException.class);
        }

        @Test
        @DisplayName("should include the duplicate email in the exception message")
        void shouldIncludeDuplicateEmailInMessage() {
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(true);

            assertThatThrownBy(() -> businessService.createBusiness(request))
                    .isInstanceOf(DuplicateBusinessEmailException.class)
                    .hasMessageContaining("ade@adestores.com");
        }

        @Test
        @DisplayName("should not call repository.save when email already exists")
        void shouldNotSaveWhenEmailExists() {
            when(businessRepository.existsByEmail("ade@adestores.com")).thenReturn(true);

            assertThrows(DuplicateBusinessEmailException.class, () -> businessService.createBusiness(request));

            verify(businessRepository, never()).save(any(Business.class));
        }
    }

    // getBusinessById

    @Nested
    @DisplayName("getBusinessById()")
    class GetBusinessByIdTests {

        private Business existingBusiness;

        @BeforeEach
        void setUp() {
            existingBusiness = buildSavedBusiness("Chioma Mart", "chioma@mart.com", "+2347011223344", "5 Abuja Road, Wuse");
        }

        @Test
        @DisplayName("should return correct id when business exists")
        void shouldReturnCorrectId() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            BusinessResponse response = businessService.getBusinessById(FIXED_ID);

            assertThat(response.id()).isEqualTo(FIXED_ID);
        }

        @Test
        @DisplayName("should return correct name when business exists")
        void shouldReturnCorrectName() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            BusinessResponse response = businessService.getBusinessById(FIXED_ID);

            assertThat(response.name()).isEqualTo("Chioma Mart");
        }

        @Test
        @DisplayName("should return correct email when business exists")
        void shouldReturnCorrectEmail() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            BusinessResponse response = businessService.getBusinessById(FIXED_ID);

            assertThat(response.email()).isEqualTo("chioma@mart.com");
        }

        @Test
        @DisplayName("should return correct phone when business exists")
        void shouldReturnCorrectPhone() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            BusinessResponse response = businessService.getBusinessById(FIXED_ID);

            assertThat(response.phone()).isEqualTo("+2347011223344");
        }

        @Test
        @DisplayName("should return correct address when business exists")
        void shouldReturnCorrectAddress() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            BusinessResponse response = businessService.getBusinessById(FIXED_ID);

            assertThat(response.address()).isEqualTo("5 Abuja Road, Wuse");
        }

        @Test
        @DisplayName("should call repository.findById with the exact UUID provided")
        void shouldCallFindByIdWithCorrectUuid() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            businessService.getBusinessById(FIXED_ID);

            verify(businessRepository).findById(FIXED_ID);
        }

        @Test
        @DisplayName("should throw BusinessNotFoundException when no business exists for the given id")
        void shouldThrowWhenNotFound() {
            UUID unknownId = UUID.randomUUID();
            when(businessRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.getBusinessById(unknownId))
                    .isInstanceOf(BusinessNotFoundException.class);
        }

        @Test
        @DisplayName("should include the missing id in the BusinessNotFoundException message")
        void shouldIncludeMissingIdInMessage() {
            UUID unknownId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(businessRepository.findById(unknownId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.getBusinessById(unknownId))
                    .isInstanceOf(BusinessNotFoundException.class)
                    .hasMessageContaining("99999999-9999-9999-9999-999999999999");
        }

        @Test
        @DisplayName("should not call save when retrieving a business by id")
        void shouldNotCallSaveOnGet() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));

            businessService.getBusinessById(FIXED_ID);

            verify(businessRepository, never()).save(any());
        }
    }

    // getAllBusinesses

    @Nested
    @DisplayName("getAllBusinesses()")
    class GetAllBusinessesTests {

        @Test
        @DisplayName("should return a list containing all businesses")
        void shouldReturnAllBusinesses() {
            Business b1 = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            Business b2 = buildSavedBusiness("Chioma Mart", "chioma@mart.com", "+2347011223344", "5 Abuja Road");
            b2.setId(UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"));
            when(businessRepository.findAll()).thenReturn(List.of(b1, b2));

            List<BusinessResponse> result = businessService.getAllBusinesses();

            assertThat(result).hasSize(2);
            assertThat(result).extracting(BusinessResponse::email)
                    .containsExactly("ade@adestores.com", "chioma@mart.com");
        }

        @Test
        @DisplayName("should return an empty list when no businesses exist")
        void shouldReturnEmptyList() {
            when(businessRepository.findAll()).thenReturn(List.of());

            List<BusinessResponse> result = businessService.getAllBusinesses();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("should call repository.findAll exactly once")
        void shouldCallFindAllOnce() {
            when(businessRepository.findAll()).thenReturn(List.of());

            businessService.getAllBusinesses();

            verify(businessRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("should map each business entity to a BusinessResponse")
        void shouldMapEveryBusinessToResponse() {
            Business b1 = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            Business b2 = buildSavedBusiness("Emeka Ventures", "emeka@ventures.com", "+2348099887766", "3 Port Harcourt Road");
            b2.setId(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"));
            when(businessRepository.findAll()).thenReturn(List.of(b1, b2));

            List<BusinessResponse> result = businessService.getAllBusinesses();

            assertThat(result).extracting(BusinessResponse::name)
                    .containsExactly("Ade Stores", "Emeka Ventures");
        }
    }

    // updateBusiness

    @Nested
    @DisplayName("updateBusiness()")
    class UpdateBusinessTests {

        private Business existingBusiness;
        private UpdateBusinessRequest updateRequest;

        @BeforeEach
        void setUp() {
            existingBusiness = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja");
            updateRequest = new UpdateBusinessRequest(
                    "Ade Stores Updated",
                    "ade.updated@adestores.com",
                    "+2348099998888",
                    "15 Victoria Island, Lagos"
            );
        }

        @Test
        @DisplayName("should return updated name")
        void shouldReturnUpdatedName() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(false);
            Business afterSave = buildSavedBusiness("Ade Stores Updated", "ade.updated@adestores.com", "+2348099998888", "15 Victoria Island, Lagos");
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.updateBusiness(FIXED_ID, updateRequest);

            assertThat(response.name()).isEqualTo("Ade Stores Updated");
        }

        @Test
        @DisplayName("should return updated email")
        void shouldReturnUpdatedEmail() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(false);
            Business afterSave = buildSavedBusiness("Ade Stores Updated", "ade.updated@adestores.com", "+2348099998888", "15 Victoria Island, Lagos");
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.updateBusiness(FIXED_ID, updateRequest);

            assertThat(response.email()).isEqualTo("ade.updated@adestores.com");
        }

        @Test
        @DisplayName("should return updated phone")
        void shouldReturnUpdatedPhone() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(false);
            Business afterSave = buildSavedBusiness("Ade Stores Updated", "ade.updated@adestores.com", "+2348099998888", "15 Victoria Island, Lagos");
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.updateBusiness(FIXED_ID, updateRequest);

            assertThat(response.phone()).isEqualTo("+2348099998888");
        }

        @Test
        @DisplayName("should return updated address")
        void shouldReturnUpdatedAddress() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(false);
            Business afterSave = buildSavedBusiness("Ade Stores Updated", "ade.updated@adestores.com", "+2348099998888", "15 Victoria Island, Lagos");
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.updateBusiness(FIXED_ID, updateRequest);

            assertThat(response.address()).isEqualTo("15 Victoria Island, Lagos");
        }

        @Test
        @DisplayName("should not modify active flag during update")
        void shouldNotModifyActiveFlag() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.updateBusiness(FIXED_ID, updateRequest);

            verify(businessRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should allow keeping the same email without a duplicate check")
        void shouldAllowKeepingSameEmail() {
            UpdateBusinessRequest sameEmailRequest = new UpdateBusinessRequest(
                    "Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street, Ikeja"
            );
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.save(any(Business.class))).thenReturn(existingBusiness);

            BusinessResponse response = businessService.updateBusiness(FIXED_ID, sameEmailRequest);

            assertThat(response.email()).isEqualTo("ade@adestores.com");
            verify(businessRepository, never()).existsByEmail(any());
        }

        @Test
        @DisplayName("should throw BusinessNotFoundException when business does not exist")
        void shouldThrowWhenNotFound() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.updateBusiness(FIXED_ID, updateRequest))
                    .isInstanceOf(BusinessNotFoundException.class);

            verify(businessRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw DuplicateBusinessEmailException when new email belongs to another business")
        void shouldThrowWhenNewEmailIsTaken() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(existingBusiness));
            when(businessRepository.existsByEmail("ade.updated@adestores.com")).thenReturn(true);

            assertThatThrownBy(() -> businessService.updateBusiness(FIXED_ID, updateRequest))
                    .isInstanceOf(DuplicateBusinessEmailException.class)
                    .hasMessageContaining("ade.updated@adestores.com");

            verify(businessRepository, never()).save(any());
        }
    }

    // activateBusiness

    @Nested
    @DisplayName("activateBusiness()")
    class ActivateBusinessTests {

        @Test
        @DisplayName("should return active=true in the response after activation")
        void shouldActivateBusiness() {
            Business inactive = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            inactive.setActive(false);
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(inactive));
            Business afterSave = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            afterSave.setActive(true);
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.activateBusiness(FIXED_ID);

            assertTrue(response.active());
        }

        @Test
        @DisplayName("should persist active=true on the entity")
        void shouldPersistActiveFlagAsTrue() {
            Business inactive = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            inactive.setActive(false);
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(inactive));
            when(businessRepository.save(any(Business.class))).thenReturn(inactive);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.activateBusiness(FIXED_ID);

            verify(businessRepository).save(captor.capture());
            assertTrue(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should throw BusinessNotFoundException when business does not exist")
        void shouldThrowWhenNotFound() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.activateBusiness(FIXED_ID))
                    .isInstanceOf(BusinessNotFoundException.class);

            verify(businessRepository, never()).save(any());
        }

        @Test
        @DisplayName("should call repository.save exactly once")
        void shouldCallSaveOnce() {
            Business business = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            business.setActive(false);
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(business));
            when(businessRepository.save(any(Business.class))).thenReturn(business);

            businessService.activateBusiness(FIXED_ID);

            verify(businessRepository, times(1)).save(any(Business.class));
        }
    }

    // deactivateBusiness

    @Nested
    @DisplayName("deactivateBusiness()")
    class DeactivateBusinessTests {

        @Test
        @DisplayName("should return active=false in the response after deactivation")
        void shouldDeactivateBusiness() {
            Business active = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(active));
            Business afterSave = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            afterSave.setActive(false);
            when(businessRepository.save(any(Business.class))).thenReturn(afterSave);

            BusinessResponse response = businessService.deactivateBusiness(FIXED_ID);

            assertFalse(response.active());
        }

        @Test
        @DisplayName("should persist active=false on the entity")
        void shouldPersistActiveFlagAsFalse() {
            Business active = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(active));
            when(businessRepository.save(any(Business.class))).thenReturn(active);
            ArgumentCaptor<Business> captor = ArgumentCaptor.forClass(Business.class);

            businessService.deactivateBusiness(FIXED_ID);

            verify(businessRepository).save(captor.capture());
            assertFalse(captor.getValue().isActive());
        }

        @Test
        @DisplayName("should throw BusinessNotFoundException when business does not exist")
        void shouldThrowWhenNotFound() {
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> businessService.deactivateBusiness(FIXED_ID))
                    .isInstanceOf(BusinessNotFoundException.class);

            verify(businessRepository, never()).save(any());
        }

        @Test
        @DisplayName("should call repository.save exactly once")
        void shouldCallSaveOnce() {
            Business business = buildSavedBusiness("Ade Stores", "ade@adestores.com", "+2348012345678", "12 Lagos Street");
            when(businessRepository.findById(FIXED_ID)).thenReturn(Optional.of(business));
            when(businessRepository.save(any(Business.class))).thenReturn(business);

            businessService.deactivateBusiness(FIXED_ID);

            verify(businessRepository, times(1)).save(any(Business.class));
        }
    }

    // toResponse mapping

    @Nested
    @DisplayName("toResponse() mapping (observed via createBusiness and getBusinessById)")
    class ToResponseMappingTests {

        @Test
        @DisplayName("should map all seven BusinessResponse fields correctly through createBusiness")
        void shouldMapAllFieldsViaCreateBusiness() {
            UUID id = UUID.fromString("cafebabe-cafe-babe-cafe-babecafebabe");
            Instant createdAt = Instant.parse("2025-06-15T08:30:00Z");

            Business saved = new Business();
            saved.setId(id);
            saved.setName("Ngozi Foods");
            saved.setEmail("ngozi@foods.com");
            saved.setPhone("+2348055554444");
            saved.setAddress("10 Enugu Road, GRA");
            saved.setActive(true);
            saved.setCreatedAt(createdAt);

            CreateBusinessRequest req = new CreateBusinessRequest(
                    "Ngozi Foods", "ngozi@foods.com", "+2348055554444", "10 Enugu Road, GRA"
            );
            when(businessRepository.existsByEmail("ngozi@foods.com")).thenReturn(false);
            when(businessRepository.save(any(Business.class))).thenReturn(saved);

            BusinessResponse response = businessService.createBusiness(req);

            assertAll("all BusinessResponse fields from createBusiness",
                    () -> assertThat(response.id()).isEqualTo(id),
                    () -> assertThat(response.name()).isEqualTo("Ngozi Foods"),
                    () -> assertThat(response.email()).isEqualTo("ngozi@foods.com"),
                    () -> assertThat(response.phone()).isEqualTo("+2348055554444"),
                    () -> assertThat(response.address()).isEqualTo("10 Enugu Road, GRA"),
                    () -> assertTrue(response.active()),
                    () -> assertThat(response.createdAt()).isEqualTo(createdAt)
            );
        }

        @Test
        @DisplayName("should map all seven BusinessResponse fields correctly through getBusinessById")
        void shouldMapAllFieldsViaGetBusinessById() {
            UUID id = UUID.fromString("deadbeef-dead-beef-dead-beefdeadbeef");
            Instant createdAt = Instant.parse("2025-12-31T23:59:59Z");

            Business stored = new Business();
            stored.setId(id);
            stored.setName("Tunde Tech");
            stored.setEmail("tunde@tech.ng");
            stored.setPhone("+2348077776666");
            stored.setAddress("22 Kano Road, Kaduna");
            stored.setActive(true);
            stored.setCreatedAt(createdAt);

            when(businessRepository.findById(id)).thenReturn(Optional.of(stored));

            BusinessResponse response = businessService.getBusinessById(id);

            assertAll("all BusinessResponse fields from getBusinessById",
                    () -> assertThat(response.id()).isEqualTo(id),
                    () -> assertThat(response.name()).isEqualTo("Tunde Tech"),
                    () -> assertThat(response.email()).isEqualTo("tunde@tech.ng"),
                    () -> assertThat(response.phone()).isEqualTo("+2348077776666"),
                    () -> assertThat(response.address()).isEqualTo("22 Kano Road, Kaduna"),
                    () -> assertTrue(response.active()),
                    () -> assertThat(response.createdAt()).isEqualTo(createdAt)
            );
        }
    }
}
