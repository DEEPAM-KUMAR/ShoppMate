package com.omatheusmesmo.shoppmate.list.service;

import com.omatheusmesmo.shoppmate.list.entity.ShoppingList;
import com.omatheusmesmo.shoppmate.list.repository.ShoppingListRepository;
import com.omatheusmesmo.shoppmate.shared.service.AuditService;
import com.omatheusmesmo.shoppmate.user.entity.User;
import com.omatheusmesmo.shoppmate.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ShoppingListService shoppingListService;

    private ShoppingList shoppingList;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("owner@example.com");
        owner.setFullName("Owner");
        owner.setPassword("pass");

        shoppingList = new ShoppingList();
        shoppingList.setId(1L);
        shoppingList.setName("Weekly");
        shoppingList.setOwner(owner);
    }

    @Test
    void saveList() {
        when(userService.findUser(owner.getId())).thenReturn(owner);
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);

        ShoppingList saved = shoppingListService.saveList(shoppingList);

        assertNotNull(saved);
        verify(auditService, times(1)).setAuditData(shoppingList, true);
        verify(shoppingListRepository, times(1)).save(shoppingList);
    }

    @Test
    void findListById() {
        when(shoppingListRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(shoppingList));

        ShoppingList found = shoppingListService.findListById(1L);

        assertEquals(1L, found.getId());
    }

    @Test
    void findListById_NotFound() {
        when(shoppingListRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> shoppingListService.findListById(1L));
    }

    @Test
    void findAll() {
        when(shoppingListRepository.findAll()).thenReturn(List.of(shoppingList));

        List<ShoppingList> lists = shoppingListService.findAll();

        assertEquals(1, lists.size());
        verify(shoppingListRepository, times(1)).findAll();
    }
}
