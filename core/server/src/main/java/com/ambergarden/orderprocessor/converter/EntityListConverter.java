package com.ambergarden.orderprocessor.converter;

import java.util.List;

/**
 * Base interface for entity list converters
 *
 * @param <S> Model object retrieved from databases
 * @param <T> Data transfer object/business object used in business logic
 */
public interface EntityListConverter<S, T> extends EntityConverter<S, T> {
   /**
    * Transforms model object list retrieved from database to data transfer
    * object/business object list used in business logic
    *
    * @param moList the source model object collection
    * @return the converted data transfer object list
    */
   List<T> convertListFrom(Iterable<S> moList);

   /**
    * Transforms data transfer object/business object list used in business logic
    * to model object list to be saved in database
    *
    * @param dto the data transfer object collection
    * @return the converted model object list
    */
   List<S> convertListTo(Iterable<T> dtoList);
}