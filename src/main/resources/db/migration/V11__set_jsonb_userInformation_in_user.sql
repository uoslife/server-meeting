ALTER TABLE "user"
    ADD COLUMN IF NOT EXISTS "user_personal_information" jsonb,
    DROP COLUMN IF EXISTS "department",
    DROP COLUMN IF EXISTS "birth_year",
    DROP COLUMN IF EXISTS "gender",
    DROP COLUMN IF EXISTS "height",
    DROP COLUMN IF EXISTS "kakao_talk_id",
    DROP COLUMN IF EXISTS "student_type",
    DROP COLUMN IF EXISTS "university",
    DROP COLUMN IF EXISTS "student_number",
    DROP COLUMN IF EXISTS "smoking",
    DROP COLUMN IF EXISTS "spirit_animal",
    DROP COLUMN IF EXISTS "mbti",
    DROP COLUMN IF EXISTS "interest";
