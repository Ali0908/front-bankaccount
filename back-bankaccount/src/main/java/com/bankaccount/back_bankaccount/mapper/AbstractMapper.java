package com.bankaccount.back_bankaccount.mapper;

import java.util.List;

/**
 * Interface générique pour les mappers Entity <-> DTO
 * @param <E> Type de l'entité
 * @param <D> Type du DTO
 */
public interface AbstractMapper<E, D> {
    
    /**
     * Convertit une entité en DTO
     * @param entity l'entité à convertir
     * @return le DTO correspondant
     */
    D toDto(E entity);
    
    /**
     * Convertit un DTO en entité
     * @param dto le DTO à convertir
     * @return l'entité correspondante
     */
    E toEntity(D dto);
    
    /**
     * Convertit une liste d'entités en liste de DTOs
     * @param entities la liste d'entités à convertir
     * @return la liste de DTOs correspondante
     */
    List<D> toDtoList(List<E> entities);
    
    /**
     * Convertit une liste de DTOs en liste d'entités
     * @param dtos la liste de DTOs à convertir
     * @return la liste d'entités correspondante
     */
    List<E> toEntityList(List<D> dtos);
}
