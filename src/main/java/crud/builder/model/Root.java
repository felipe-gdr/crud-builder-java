package crud.builder.model;

import com.google.common.base.Objects;

import java.util.List;

public class Root {
    private List<Entity> entities;

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public static class Entity {
        private String name;
        private List<Field> fields;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Field> getFields() {
            return fields;
        }

        public void setFields(List<Field> fields) {
            this.fields = fields;
        }

        @Override
        public String toString() {
            return "Entity{" +
                    "name='" + name + '\'' +
                    ", fields=" + fields +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Entity entity = (Entity) o;
            return Objects.equal(name, entity.name) &&
                    Objects.equal(fields, entity.fields);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, fields);
        }
    }

    public static class Field {
        private FieldType type;
        private String name;
        private boolean required;
        private String entity;

        public enum FieldType {
            STRING,
            BOOLEAN,
            INTEGER,
            ONE_TO_MANY,
            ONE_TO_ONE,
            MANY_TO_ONE,
            MANY_TO_MANY;

            public static boolean isRelationship(FieldType fieldType) {
                return fieldType == ONE_TO_MANY
                        || fieldType == ONE_TO_ONE
                        || fieldType == MANY_TO_ONE
                        || fieldType == MANY_TO_MANY;
            }
        }

        public FieldType getType() {
            return type;
        }

        public void setType(FieldType type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean getRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public String getEntity() {
            return entity;
        }

        public void setEntity(String entity) {
            this.entity = entity;
        }

        @Override
        public String toString() {
            return "Field{" +
                    "type=" + type +
                    ", name='" + name + '\'' +
                    ", required=" + required +
                    ", entity='" + entity + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Field field = (Field) o;
            return type == field.type &&
                    Objects.equal(name, field.name) &&
                    Objects.equal(required, field.required) &&
                    Objects.equal(entity, field.entity);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(type, name, required, entity);
        }
    }

    @Override
    public String toString() {
        return "Root{" +
                "entities=" + entities +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Root root = (Root) o;
        return Objects.equal(entities, root.entities);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entities);
    }
}

