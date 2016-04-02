package com.ambergarden.orderprocessor.converter;

/**
 * Base interface for entity converters
 *
 * @param <S> Model object retrieved from databases
 * @param <T> Data transfer object/business object used in business logic
 */
public interface EntityConverter<S, T> {
   /**
    * Transforms model object retrieved from database to data transfer
    * object/business object used in business logic
    *
    * @param mo the source model object
    * @return the converted data transfer object
    */
   T convertFrom(S mo);

   /**
    * Transforms data transfer object/business object used in business logic
    * to model object saved in database
    *
    * @param dto the data transfer object
    * @return the converted model object
    */
   S convertTo(T dto);
}