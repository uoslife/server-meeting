package uoslife.servermeeting.domain.user.domain.entity.enums

enum class DepartmentNameType(val college: String, val department: String, val code: Int) {
    ADMINISTRATION("정경대학", "행정학과", 120),
    INTERNATIONAL_RELATIONS("정경대학", "국제관계학과", 150),
    ECONOMICS("정경대학", "경제학부", 280),
    SOCIAL_WELFARE("정경대학", "사회복지 학과", 910),
    TAXATION("정경대학", "세무학과", 830),
    BUSINESS("경영대학", "경영학부", 270),
    CHEMICAL_ENGINEERING("공과대학", "화학공학과", 340),
    MECHATRONICS("공과대학", "기계정보공학과", 430),
    MATERIALS_ENGINEERING("공과대학", "신소재공학과", 450),
    ELECTRICAL_ENGINEERING("공과대학", "전기전자컴퓨터공학부", 440),
    CIVIL_ENGINEERING("공과대학", "토목공학과", 860),
    COMPUTER_SCIENCE("공과대학", "컴퓨터과학부", 920),
    ENVIRONMENTAL_HORTICULTURE("자연과학대학", "환경원예학과", 520),
    MATHEMATICS("자연과학대학", "수학과", 540),
    PHYSICS("자연과학대학", "물리학과", 550),
    LIFE_SCIENCES("자연과학대학", "생명과학과", 560),
    STATISTICS("자연과학대학", "통계학과", 580),
    ENGLISH_LITERATURE("인문대학", "영어영문학과", 610),
    KOREAN_LITERATURE("인문대학", "국어국문학과", 620),
    HISTORY("인문대학", "국사학과", 630),
    PHILOSOPHY("인문대학", "철학과", 640),
    CHINESE_CULTURE("인문대학", "중국어문화학과", 650),
    URBAN_ADMINISTRATION("도시과학대학", "도시행정학과", 810),
    URBAN_SOCIOLOGY("도시과학대학", "도시사회학과", 840),
    GEOINFORMATION_ENGINEERING("도시과학대학", "공간정보공학과", 930),
    ARCHITECTURE_ARCHITECTURAL("도시과학대학", "건축학부-건축학과", 871),
    ARCHITECTURE_ENGINEERING("도시과학대학", "건축학부-건축공학과", 872),
    URBAN_ENGINEERING("도시과학대학", "도시공학과", 873),
    TRANSPORTATION_ENGINEERING("도시과학대학", "교통공학과", 874),
    LANDSCAPE_ARCHITECTURE("도시과학대학", "조경학과", 875),
    ENVIRONMENTAL_ENGINEERING("도시과학대학", "환경공학부", 890),
    MUSIC("예술체육대학", "음악학과", 710),
    INDUSTRIAL_DESIGN_VISUAL("예술체육대학", "산업디자인-시각", 720),
    INDUSTRIAL_DESIGN_INDUSTRIAL("예술체육대학", "산업디자인-공업", 720),
    ENVIRONMENTAL_SCULPTURE("예술체육대학", "환경조각학과", 730),
    SPORTS_SCIENCE("예술체육대학", "스포츠과학과", 750),
    LIBERAL_ARTS("자유전공대학", "자유전공학부", 940),
    APPLIED_CHEMISTRY("자연과학대학", "응용화학과", 530),
    ARTIFICIAL_INTELLIGENCE("공과대학", "인공지능학과", 480),
}
