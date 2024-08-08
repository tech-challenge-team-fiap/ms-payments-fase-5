terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "5.40.0"
    }
  }

  required_version = ">= 1.0.0"
}

data "aws_caller_identity" "current" {}
data "aws_region" "current" {}

#ECS Setting
resource "aws_ecs_cluster" "api_payments_cluster" {
  name = "api-payments-cluster"
}
#ECS Setting

resource "aws_ecs_task_definition" "api_payments_task" {
  family                   = "api-payments-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  execution_role_arn       = aws_iam_role.ecs_execution_role.arn
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = jsonencode(
    [
      {
        name      = "api-payments-container"
        image     = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${data.aws_region.current.name}.amazonaws.com/api-payments:latest"
        cpu : 512,
        memory : 1024,
        essential : true,
        portMappings = [
          {
            containerPort = 80
            hostPort      = 80
            protocol      = "tcp"
          },
          {
            containerPort = 8080
            hostPort      = 8080
            protocol      = "tcp"
          }
        ]
      }
    ]
  )
}

resource "aws_iam_role_policy_attachment" "ecs_execution_policy_attachment" {
  role       = aws_iam_role.ecs_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
}

resource "aws_iam_role" "ecs_execution_role" {
  name = "ecs_execution_role"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Effect    = "Allow",
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        },
        Action = "sts:AssumeRole"
      }
    ]
  })
}

#ECS Setting

# ECS SERVICE
resource "aws_ecs_service" "api_payments_service" {
  name            = "api-payments-service"
  cluster         = aws_ecs_cluster.api_payments_cluster.id
  task_definition = aws_ecs_task_definition.api_payments_task.arn
  launch_type     = "FARGATE"
  desired_count   =  2

  network_configuration {
    subnets          = ["subnet-0de1ca34acd9dea27"]
    security_groups  = [aws_security_group.api_payments_sg.id]
    assign_public_ip = true
  }
}

resource "aws_security_group" "api_payments_sg" {
  name        = "api-payments-sg"
  description = "Allow all inbound traffic"
  vpc_id      = "vpc-0bd1f59a1daae83c1"  # Consider dynamic retrieval

  ingress {
    description      = "Allow all inbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }

  egress {
    description      = "Allow all outbound traffic"
    from_port        = 0
    to_port          = 0
    protocol         = "-1"
    cidr_blocks      = ["0.0.0.0/0"]
  }
}