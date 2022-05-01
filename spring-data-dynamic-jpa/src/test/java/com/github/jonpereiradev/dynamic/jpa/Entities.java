package com.github.jonpereiradev.dynamic.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;

public class Entities {

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class Any implements Serializable {
    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class None implements Serializable {
    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class Join implements Serializable {

        @JoinColumn
        @SuppressWarnings("JpaAttributeTypeInspection")
        private None none;

    }

    @Entity
    public static class FieldEntity implements Serializable {

        @Id
        private int id;

    }

    @Entity
    public static class MethodEntity implements Serializable {

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
    public static class TransientFieldEntity implements Serializable {

        @Transient
        private int id;

    }

    @Entity
    @SuppressWarnings("JpaMissingIdInspection")
    public static class TransientMethodEntity implements Serializable {

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
    public static class BaseFieldEntity implements Serializable {

        @Id
        private int id;

    }

    @MappedSuperclass
    public static class BaseMethodEntity implements Serializable {

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
