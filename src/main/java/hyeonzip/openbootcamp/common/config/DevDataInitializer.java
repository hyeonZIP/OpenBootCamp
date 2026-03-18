package hyeonzip.openbootcamp.common.config;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements ApplicationRunner {

    private final BootcampRepository bootcampRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (bootcampRepository.count() > 0) {
            return;
        }

        // 코드스테이츠
        Bootcamp codestates = Bootcamp.builder()
            .name("코드스테이츠")
            .slug(Slug.from("codestates"))
            .logoUrl(null)
            .description("코드스테이츠는 소프트웨어 엔지니어링 부트캠프입니다.")
            .officialUrl("https://www.codestates.com")
            .build();

        codestates.addTrack(BootcampTrack.builder()
            .trackType(TrackType.FRONTEND)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.REACT, TechStack.TYPESCRIPT, TechStack.NEXT_JS))
            .priceMin(3000000).priceMax(5000000).durationWeeks(24).isRecruiting(true)
            .build());

        codestates.addTrack(BootcampTrack.builder()
            .trackType(TrackType.BACKEND)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.MYSQL))
            .priceMin(3000000).priceMax(5000000).durationWeeks(24).isRecruiting(false)
            .build());

        // 우아한테크코스
        Bootcamp woowacourse = Bootcamp.builder()
            .name("우아한테크코스")
            .slug(Slug.from("woowacourse"))
            .logoUrl(null)
            .description("우아한형제들이 운영하는 개발자 양성 교육 과정입니다.")
            .officialUrl("https://woowacourse.github.io")
            .build();

        woowacourse.addTrack(BootcampTrack.builder()
            .trackType(TrackType.BACKEND)
            .operationType(OperationType.OFFLINE)
            .techStacks(List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.MYSQL, TechStack.DOCKER))
            .priceMin(0).priceMax(0).durationWeeks(52).isRecruiting(true)
            .build());

        woowacourse.addTrack(BootcampTrack.builder()
            .trackType(TrackType.FRONTEND)
            .operationType(OperationType.OFFLINE)
            .techStacks(List.of(TechStack.JAVASCRIPT, TechStack.REACT, TechStack.TYPESCRIPT))
            .priceMin(0).priceMax(0).durationWeeks(52).isRecruiting(true)
            .build());

        // 항해99
        Bootcamp hanghae = Bootcamp.builder()
            .name("항해99")
            .slug(Slug.from("hanghae99"))
            .logoUrl(null)
            .description("99일간의 집중 개발 부트캠프입니다.")
            .officialUrl("https://hanghae99.spartacodingclub.kr")
            .build();

        hanghae.addTrack(BootcampTrack.builder()
            .trackType(TrackType.FULLSTACK)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.REACT, TechStack.NODE_JS, TechStack.MONGODB))
            .priceMin(2990000).priceMax(2990000).durationWeeks(14).isRecruiting(true)
            .build());

        hanghae.addTrack(BootcampTrack.builder()
            .trackType(TrackType.BACKEND)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.POSTGRESQL, TechStack.REDIS))
            .priceMin(2990000).priceMax(2990000).durationWeeks(14).isRecruiting(false)
            .build());

        // 패스트캠퍼스
        Bootcamp fastcampus = Bootcamp.builder()
            .name("패스트캠퍼스")
            .slug(Slug.from("fastcampus"))
            .logoUrl(null)
            .description("다양한 직군의 실무 중심 교육을 제공하는 부트캠프입니다.")
            .officialUrl("https://fastcampus.co.kr")
            .build();

        fastcampus.addTrack(BootcampTrack.builder()
            .trackType(TrackType.DATA)
            .operationType(OperationType.HYBRID)
            .techStacks(List.of(TechStack.PYTHON, TechStack.PANDAS, TechStack.SCIKIT_LEARN, TechStack.PYTORCH))
            .priceMin(2000000).priceMax(4000000).durationWeeks(20).isRecruiting(true)
            .build());

        fastcampus.addTrack(BootcampTrack.builder()
            .trackType(TrackType.DEVOPS)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.DOCKER, TechStack.KUBERNETES, TechStack.AWS, TechStack.CI_CD, TechStack.LINUX))
            .priceMin(2500000).priceMax(3500000).durationWeeks(16).isRecruiting(true)
            .build());

        // 제로베이스
        Bootcamp zerobase = Bootcamp.builder()
            .name("제로베이스")
            .slug(Slug.from("zerobase"))
            .logoUrl(null)
            .description("비전공자도 시작할 수 있는 개발 부트캠프입니다.")
            .officialUrl("https://zero-base.co.kr")
            .build();

        zerobase.addTrack(BootcampTrack.builder()
            .trackType(TrackType.FRONTEND)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.JAVASCRIPT, TechStack.REACT, TechStack.TYPESCRIPT))
            .priceMin(1490000).priceMax(1990000).durationWeeks(20).isRecruiting(true)
            .build());

        zerobase.addTrack(BootcampTrack.builder()
            .trackType(TrackType.AI_ML)
            .operationType(OperationType.ONLINE)
            .techStacks(List.of(TechStack.PYTHON, TechStack.TENSORFLOW, TechStack.PYTORCH, TechStack.PANDAS))
            .priceMin(1990000).priceMax(2490000).durationWeeks(24).isRecruiting(false)
            .build());

        bootcampRepository.saveAll(List.of(codestates, woowacourse, hanghae, fastcampus, zerobase));
    }
}
