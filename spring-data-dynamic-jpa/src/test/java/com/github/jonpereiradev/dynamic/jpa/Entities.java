package com.github.jonpereiradev.dynamic.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

public class Entities {

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class Any {
    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class None {
    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class Join {

        @JoinColumn
        @SuppressWarnings("JpaAttributeTypeInspection")
        private None none;

    }

    @Entity
    public static class FieldEntity {

        @Id
        private int id;

    }

    @Entity
    public static class MethodEntity {

        private int id;

        @Id
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class TransientFieldEntity {

        @Transient
        private int id;

    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class TransientMethodEntity {

        private int id;

        @Transient
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    @MappedSuperclass
    public static class BaseFieldEntity {

        @Id
        private int id;

    }

    @MappedSuperclass
    public static class BaseMethodEntity {

        private int id;

        @Id
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

}
